package com.da.da.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String toEmail, String orderId, String totalAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            message.setFrom("Shop Gear Admin <email_cua_ban@gmail.com>");
            message.setTo(toEmail);
            message.setSubject("Xác nhận đơn hàng #" + orderId);
            
            String content = "Xin chào,\n\n"
                    + "Cảm ơn bạn đã đặt hàng tại Shop Gear.\n"
                    + "Mã đơn hàng: " + orderId + "\n"
                    + "Tổng tiền: " + totalAmount + "\n\n"
                    + "Chúng tôi sẽ sớm liên hệ để giao hàng.\n"
                    + "Trân trọng!";
            
            message.setText(content);

            mailSender.send(message);
            System.out.println("Gửi mail thành công cho: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("Lỗi gửi mail: " + e.getMessage());
        }
    }
    public void sendOrderStatusEmail(String toEmail, Integer orderId, String status) {
        String subject = "Cập nhật trạng thái đơn hàng #" + orderId;
        String content = "Chào bạn,\n\n" +
                         "Đơn hàng #" + orderId + " của bạn đã được cập nhật trạng thái thành: " + 
                         translateStatus(status) + ".\n" +
                         "Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi!";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("email-cua-ban@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    // Hàm phụ để dịch trạng thái sang tiếng Việt cho thân thiện
    private String translateStatus(String status) {
        switch (status) {
            case "PENDING": return "Chờ xử lý";
            case "SHIPPED": return "Đang giao hàng";
            case "DELIVERED": return "Đã giao hàng thành công";
            case "CANCELLED": return "Đã bị hủy";
            default: return status;
        }
    }
}