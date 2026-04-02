package com.da.da.service;

import com.da.da.entity.Order;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentService {

    // Thông tin ngân hàng của bạn
    private final String BANK_ID = "STB"; 
    private final String ACCOUNT_NO = "050130869472"; 
    private final String ACCOUNT_NAME = "HA VAN DU";

    public String getVietQRUrl(Order order) {
        try {
            if (order == null || order.getId() == null || order.getProductTotalPrice() == null) {
                return "Lỗi: Dữ liệu đơn hàng không hợp lệ.";
            }

            // 1. CHỐNG LỖI PARSE: Xóa sạch tất cả những gì không phải là số (xóa dấu phẩy, dấu chấm, chữ đ...)
            String cleanAmount = order.getProductTotalPrice().replaceAll("[^0-9]", "");
            
            // 2. Chuyển sang Long cho chắc (số tiền không cần số lẻ phía sau)
            long finalAmount = Long.parseLong(cleanAmount);
            
            // 3. Encode các thông tin quan trọng
            String noiDungChuyenKhoan = "THANHTOAN DH" + order.getId();
            String info = URLEncoder.encode(noiDungChuyenKhoan, StandardCharsets.UTF_8.toString());
            String name = URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8.toString());
            
            // 4. Dùng nối chuỗi trực tiếp cho phần số tiền để tránh Locale làm hỏng định dạng
            String rawUrl = "https://img.vietqr.io/image/" + BANK_ID + "-" + ACCOUNT_NO + "-compact.jpg"
                          + "?amount=" + finalAmount 
                          + "&addInfo=" + info 
                          + "&accountName=" + name;
                                      
            // 5. Trả về đúng định dạng Markdown ảnh
            return rawUrl;
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi tạo mã QR: " + e.getMessage(); 
        }
    }
}