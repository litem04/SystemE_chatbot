package com.da.da.controller;

import com.da.da.entity.Order;
import com.da.da.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/webhook")
public class PaymentWebhookController {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookController.class);

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${payment.webhook.secret:techgear_secret_token_2026}")
    private String configuredSecret;

    public PaymentWebhookController(OrderRepository orderRepository,
                                    SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/vietqr")
    public ResponseEntity<?> handleVietQR(@RequestBody Map<String, Object> payload,
                                          @RequestHeader(value = "X-Webhook-Secret", required = false) String requestSecret) {
        // 1. Kiểm tra xác thực Webhook Token để ngăn chặn request giả mạo
        if (requestSecret != null && !requestSecret.equals(configuredSecret)) {
            log.warn("Cảnh báo: Webhook gọi vào với Secret Key không hợp lệ!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook secret");
        }

        try {
            String description = payload.get("description") != null ? payload.get("description").toString() : "";
            Object amountObj = payload.get("amount");
            double amount = amountObj != null ? Double.parseDouble(amountObj.toString()) : 0.0;

            Integer orderId = extractOrderId(description);
            if (orderId != null) {
                Order order = orderRepository.findById(orderId).orElse(null);
                if (order != null && "Unpaid".equalsIgnoreCase(order.getPaymentStatus())) {
                    order.setPaymentStatus("Paid");
                    order.setOrderStatus("PROCESSING");
                    orderRepository.save(order);

                    // Gửi thông báo Real-time cho Admin qua WebSocket
                    messagingTemplate.convertAndSend("/topic/admin/notifications",
                            String.format("Đơn hàng #%d đã thanh toán thành công %,.0f VND!", orderId, amount));

                    log.info("Webhook xử lý thành công: Đơn hàng #{} đã chuyển sang Paid", orderId);
                    return ResponseEntity.ok(Map.of("status", "success", "orderId", orderId));
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý Webhook VietQR: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }

        return ResponseEntity.badRequest().body("Order not found or already paid");
    }

    private Integer extractOrderId(String text) {
        if (text == null) return null;
        // Ưu tiên khớp tiền tố DH (ví dụ: DH123, THANHTOAN DH123)
        Pattern prefixPattern = Pattern.compile("(?i)DH(\\d+)");
        Matcher prefixMatcher = prefixPattern.matcher(text);
        if (prefixMatcher.find()) {
            return Integer.parseInt(prefixMatcher.group(1));
        }

        // Fallback: Tìm số nguyên đầu tiên
        Pattern numberPattern = Pattern.compile("\\d+");
        Matcher numberMatcher = numberPattern.matcher(text);
        return numberMatcher.find() ? Integer.parseInt(numberMatcher.group()) : null;
    }
}
