package com.da.da.service;

import com.da.da.dto.PaymentWebhookRequest;
import com.da.da.entity.Order;
import com.da.da.entity.enums.OrderStatus;
import com.da.da.entity.enums.PaymentStatus;
import com.da.da.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PaymentWebhookService {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookService.class);

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public PaymentWebhookService(OrderRepository orderRepository, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public boolean verifySecret(String requestSecret, String configuredSecret) {
        if (requestSecret == null || configuredSecret == null || configuredSecret.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
                requestSecret.getBytes(StandardCharsets.UTF_8),
                configuredSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Transactional
    public WebhookProcessResult processVietQrWebhook(String requestSecret, String configuredSecret, PaymentWebhookRequest payload) {
        // 1. Bảo mật: Xác thực Secret Token bắt buộc bằng Constant-Time comparison chống Timing Attack
        if (!verifySecret(requestSecret, configuredSecret)) {
            log.warn("Cảnh báo bảo mật: Webhook gọi vào với Secret Key không hợp lệ hoặc thiếu header!");
            return new WebhookProcessResult(false, 401, "Invalid or missing webhook secret", null);
        }

        if (payload == null) {
            return new WebhookProcessResult(false, 400, "Payload is required", null);
        }

        Integer orderId = extractOrderId(payload.getDescription());
        if (orderId == null) {
            return new WebhookProcessResult(false, 400, "Cannot extract order ID from description", null);
        }

        // Khóa bi quan (Pessimistic Write Lock) ngăn 2 webhook chạy song song cùng đọc trạng thái UNPAID
        Order order = orderRepository.findByIdWithLock(orderId).orElse(null);
        if (order == null) {
            return new WebhookProcessResult(false, 404, "Order #" + orderId + " not found", null);
        }

        if (order.getPaymentMode() != com.da.da.entity.enums.PaymentMode.VIETQR) {
            log.warn("Cảnh báo: Đơn hàng #{} không dùng thanh toán VietQR", orderId);
            return new WebhookProcessResult(false, 400, "Order is not using VietQR payment mode", orderId);
        }

        String transactionId = payload.getTransactionId() != null ? payload.getTransactionId() : payload.getReference();

        // 2. Chống xử lý trùng lặp (Idempotency) toàn hệ thống
        if (transactionId != null && !transactionId.isBlank()) {
            Order existingTxOrder = orderRepository.findByTransactionId(transactionId).orElse(null);
            if (existingTxOrder != null && !existingTxOrder.getId().equals(order.getId())) {
                log.error("Cảnh báo: TransactionId {} đã được sử dụng cho đơn hàng khác (#{}).", transactionId, existingTxOrder.getId());
                return new WebhookProcessResult(false, 409, "Transaction ID already used for another order", orderId);
            }
        }

        if (order.isPaid()) {
            if (transactionId != null && transactionId.equals(order.getTransactionId())) {
                log.info("Webhook: Đơn hàng #{} đã xử lý trước đó với transactionId {}. Bỏ qua xử lý trùng.", orderId, transactionId);
                return new WebhookProcessResult(true, 200, "Already processed", orderId);
            }
            log.info("Webhook: Đơn hàng #{} đã ở trạng thái PAID.", orderId);
            return new WebhookProcessResult(true, 200, "Order already paid", orderId);
        }

        // 3. Kiểm tra số tiền nhận được so với tổng tiền đơn hàng (Financial Accuracy Check)
        BigDecimal expectedAmount = order.getProductTotalPrice() != null ? order.getProductTotalPrice() : BigDecimal.ZERO;
        BigDecimal receivedAmount = payload.getAmount() != null ? payload.getAmount() : BigDecimal.ZERO;

        if (expectedAmount.compareTo(receivedAmount) != 0) {
            log.error("Sai lệch số tiền cho đơn #{}: Cần thanh toán {}, nhưng nhận được {}", orderId, expectedAmount, receivedAmount);
            return new WebhookProcessResult(false, 400, "Amount mismatch: expected " + expectedAmount + ", received " + receivedAmount, orderId);
        }

        // 4. Cập nhật trạng thái đơn hàng an toàn
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PROCESSING);
        if (transactionId != null && !transactionId.isBlank()) {
            order.setTransactionId(transactionId);
        }
        orderRepository.save(order);

        // 5. Gửi thông báo Real-time cho Admin qua WebSocket
        try {
            messagingTemplate.convertAndSend("/topic/admin/notifications",
                    String.format("Đơn hàng #%d đã thanh toán thành công %,.0f VND!", orderId, receivedAmount.doubleValue()));
        } catch (Exception e) {
            log.warn("Không thể gửi thông báo WebSocket: {}", e.getMessage());
        }

        log.info("Webhook xử lý thành công: Đơn hàng #{} đã cập nhật PAID với transactionId {}", orderId, transactionId);
        return new WebhookProcessResult(true, 200, "Success", orderId);
    }

    public Integer extractOrderId(String text) {
        if (text == null) return null;
        Pattern exactPattern = Pattern.compile("(?i)THANHTOAN\\s*DH(\\d+)");
        Matcher exactMatcher = exactPattern.matcher(text);
        if (exactMatcher.find()) {
            return Integer.parseInt(exactMatcher.group(1));
        }
        return null;
    }

    public record WebhookProcessResult(boolean success, int httpStatus, String message, Integer orderId) {
        public Map<String, Object> toResponseMap() {
            if (success) {
                return Map.of("status", "success", "message", message, "orderId", orderId != null ? orderId : 0);
            } else {
                return Map.of("status", "error", "message", message);
            }
        }
    }
}
