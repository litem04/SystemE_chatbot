package com.da.da.service;

import com.da.da.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${payment.vietqr.bank-id:STB}")
    private String bankId;

    @Value("${payment.vietqr.account-no:050130869472}")
    private String accountNo;

    @Value("${payment.vietqr.account-name:HA VAN DU}")
    private String accountName;

    public String getVietQRUrl(Order order) {
        try {
            if (order == null || order.getId() == null) {
                return "Lỗi: Dữ liệu đơn hàng không hợp lệ.";
            }

            BigDecimal total = order.getProductTotalPrice() != null ? order.getProductTotalPrice() : BigDecimal.ZERO;
            long finalAmount = total.longValue();

            String transferContent = "THANHTOAN DH" + order.getId();
            String info = URLEncoder.encode(transferContent, StandardCharsets.UTF_8);
            String name = URLEncoder.encode(accountName, StandardCharsets.UTF_8);

            return "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-compact.jpg"
                    + "?amount=" + finalAmount
                    + "&addInfo=" + info
                    + "&accountName=" + name;

        } catch (Exception e) {
            log.error("Lỗi khi tạo mã VietQR cho đơn hàng #{}: ", order.getId(), e);
            return "Lỗi khi tạo mã QR: " + e.getMessage();
        }
    }
}