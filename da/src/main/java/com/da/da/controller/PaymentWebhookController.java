package com.da.da.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.da.entity.Order;
import com.da.da.repository.OrderRepository;

@RestController
@RequestMapping("/api/webhook")
public class PaymentWebhookController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private SimpMessagingTemplate messagingTemplate; // Dùng cho WebSocket

    @PostMapping("/vietqr")
    public ResponseEntity<?> handleVietQR(@RequestBody Map<String, Object> payload) {
        // 1. Lấy nội dung chuyển khoản và số tiền từ payload (tùy bên cung cấp Webhook)
        String description = (String) payload.get("description"); 
        Double amount = Double.parseDouble(payload.get("amount").toString());

        // 2. Tìm mã đơn hàng trong nội dung (Ví dụ nội dung là "DH123")
        Integer orderId = extractOrderId(description); 
        
        if (orderId != null) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null && "Unpaid".equals(order.getPaymentStatus())) {
                
                // 3. Cập nhật trạng thái tự động
                order.setPaymentStatus("Paid");
                order.setOrderStatus("PROCESSING"); // Chuyển sang "Đang xử lý" luôn
                orderRepository.save(order);

                // 4. Bắn thông báo Real-time cho Admin
                messagingTemplate.convertAndSend("/topic/admin/notifications", 
                    "Đơn hàng #" + orderId + " đã thanh toán thành công " + amount + " VND!");

                return ResponseEntity.ok("Success");
            }
        }
        return ResponseEntity.badRequest().build();
    }

    private Integer extractOrderId(String text) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Integer.parseInt(matcher.group()) : null;
    }
}
