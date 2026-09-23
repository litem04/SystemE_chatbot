package com.da.da.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@techgear.vn}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmation(String toEmail, String orderId, String totalAmount) {
        if (toEmail == null || toEmail.trim().isEmpty()) return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("TechGear Store <" + senderEmail + ">");
            message.setTo(toEmail);
            message.setSubject("Xác nhận đơn hàng #" + orderId + " tại TechGear");

            String content = "Xin chào,\n\n"
                    + "Cảm ơn bạn đã đặt hàng tại TechGear Store.\n"
                    + "Mã đơn hàng: #" + orderId + "\n"
                    + "Tổng giá trị đơn hàng: " + totalAmount + "\n\n"
                    + "Đơn hàng của bạn đang được chúng tôi xử lý và sẽ sớm được giao đến bạn.\n"
                    + "Trân trọng,\nĐội ngũ TechGear Store.";

            message.setText(content);
            mailSender.send(message);
            log.info("Đã gửi email xác nhận thành công cho đơn hàng #{} tới {}", orderId, toEmail);

        } catch (Exception e) {
            log.warn("Không thể gửi email xác nhận cho {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendOrderStatusEmail(String toEmail, Integer orderId, String status) {
        if (toEmail == null || toEmail.trim().isEmpty()) return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("TechGear Store <" + senderEmail + ">");
            message.setTo(toEmail);
            message.setSubject("Cập nhật trạng thái đơn hàng #" + orderId);

            String content = "Xin chào,\n\n"
                    + "Đơn hàng #" + orderId + " của bạn đã được cập nhật trạng thái thành: "
                    + translateStatus(status) + ".\n"
                    + "Cảm ơn bạn đã mua sắm tại TechGear Store!\n\n"
                    + "Trân trọng,\nĐội ngũ TechGear Store.";

            message.setText(content);
            mailSender.send(message);
            log.info("Đã gửi email trạng thái đơn hàng #{} tới {}", orderId, toEmail);

        } catch (Exception e) {
            log.warn("Không thể gửi email trạng thái cho {}: {}", toEmail, e.getMessage());
        }
    }

    private String translateStatus(String status) {
        if (status == null) return "";
        switch (status.toUpperCase()) {
            case "PENDING": return "Chờ xử lý";
            case "WAITING_FOR_PAYMENT": return "Chờ thanh toán";
            case "PROCESSING": return "Đang đóng gói";
            case "SHIPPED": return "Đang giao hàng";
            case "DELIVERED": return "Đã giao hàng thành công";
            case "CANCELLED": return "Đã bị hủy";
            default: return status;
        }
    }
}