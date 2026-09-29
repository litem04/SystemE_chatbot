package com.da.da.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatbotManager {

    private static final Logger log = LoggerFactory.getLogger(ChatbotManager.class);

    private final DigitalStoreAssistant geminiAssistant;
    private final DigitalStoreAssistant ollamaAssistant;
    private final com.da.da.repository.ProductRepository productRepository;

    public ChatbotManager(@Qualifier("geminiAssistant") DigitalStoreAssistant geminiAssistant,
                          @Qualifier("ollamaAssistant") DigitalStoreAssistant ollamaAssistant,
                          com.da.da.repository.ProductRepository productRepository) {
        this.geminiAssistant = geminiAssistant;
        this.ollamaAssistant = ollamaAssistant;
        this.productRepository = productRepository;
    }

    public String processChat(String memoryId, String message) {
        try {
            return geminiAssistant.chat(memoryId, message);
        } catch (Exception e) {
            log.warn("Gemini gặp sự cố ({}), tự động chuyển sang mô hình Ollama dự phòng...", e.getMessage());
            try {
                return ollamaAssistant.chat(memoryId, message) + "\n\n*(Phản hồi từ mô hình dự phòng)*";
            } catch (Exception ex) {
                log.warn("Cả hai mô hình AI Gemini và Ollama đều không khả dụng. Kích hoạt bộ trợ lý thông minh TechGear nội bộ.");
                return fallbackSmartReply(message);
            }
        }
    }

    private String fallbackSmartReply(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Dạ, TechGear có thể giúp gì cho bạn hôm nay ạ?";
        }

        String lower = message.trim().toLowerCase();

        // 1. Chào hỏi (tránh nhầm chữ 'hi' trong 'nhiêu')
        if (lower.matches(".*\\b(alo|chào|hi|hello|hey|shop ơi|ad ơi|bạn ơi)\\b.*") 
                && !lower.contains("giá") && !lower.contains("nhiêu") && !lower.contains("mua") && lower.length() < 25) {
            return "Dạ chào bạn! Mình là Trợ lý TechGear. Bạn đang cần tìm kiếm Laptop, Điện thoại, Phụ kiện công nghệ hay muốn kiểm tra đơn hàng nào ạ?";
        }

        // 2. Tra cứu đơn hàng
        if (lower.contains("đơn hàng") || lower.contains("tra cứu đơn") || lower.contains("mã đơn") || lower.contains("kiểm tra đơn")) {
            return "Để theo dõi tiến độ vận chuyển và thanh toán các đơn hàng của mình, bạn hãy vào mục [Đơn hàng của tôi](/my-orders) nhé! Nếu có đơn hàng VietQR chưa trả, bạn có thể bấm 'Thanh toán' để quét mã QR bất kỳ lúc nào.";
        }

        // 3. Tìm kiếm sản phẩm thông minh dựa trên database TechGear
        java.util.List<com.da.da.entity.Product> allProducts = productRepository.findAll();
        java.util.List<com.da.da.entity.Product> matched = new java.util.ArrayList<>();

        boolean searchLaptop = lower.contains("laptop") || lower.contains("máy tính") || lower.contains("macbook");
        boolean searchPhuKien = lower.contains("phụ kiện") || lower.contains("chuột") || lower.contains("bàn phím") || lower.contains("tai nghe");
        boolean searchMobile = lower.contains("điện thoại") || lower.contains("phone") || lower.contains("iphone") || lower.contains("samsung");

        for (com.da.da.entity.Product p : allProducts) {
            String pName = (p.getName() != null ? p.getName() : "").toLowerCase();
            String pCat = (p.getProductCategory() != null ? p.getProductCategory() : "").toLowerCase();

            if (lower.contains("iphone") && pName.contains("iphone")) {
                matched.add(p);
            } else if (lower.contains("rog") && pName.contains("rog")) {
                matched.add(p);
            } else if (lower.contains("macbook") && pName.contains("macbook")) {
                matched.add(p);
            } else if (lower.contains("akko") && pName.contains("akko")) {
                matched.add(p);
            } else if (lower.contains("logitech") && pName.contains("logitech")) {
                matched.add(p);
            } else if (lower.contains("sony") && pName.contains("sony")) {
                matched.add(p);
            } else if (searchLaptop && ("laptop".equalsIgnoreCase(pCat) || pName.contains("laptop") || pName.contains("macbook") || pName.contains("rog"))) {
                if (!matched.contains(p)) matched.add(p);
            } else if (searchPhuKien && ("accessories".equalsIgnoreCase(pCat) || pName.contains("chuột") || pName.contains("bàn phím") || pName.contains("tai nghe"))) {
                if (!matched.contains(p)) matched.add(p);
            } else if (searchMobile && ("mobile".equalsIgnoreCase(pCat) || pName.contains("phone"))) {
                if (!matched.contains(p)) matched.add(p);
            } else {
                for (String word : lower.split("\\s+")) {
                    if (word.length() >= 3 && pName.contains(word)) {
                        if (!matched.contains(p)) matched.add(p);
                    }
                }
            }
        }

        java.text.NumberFormat vnCur = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        if (!matched.isEmpty()) {
            StringBuilder sb = new StringBuilder("Dạ, TechGear tìm thấy các sản phẩm phù hợp với yêu cầu của bạn:\n\n");
            int count = 0;
            for (com.da.da.entity.Product p : matched) {
                if (count++ >= 4) break;
                String priceStr = p.getPrice() != null ? vnCur.format(p.getPrice()) : "Liên hệ";
                String stockStr = (p.getStock() != null && p.getStock() > 0) ? ("Còn " + p.getStock() + " sản phẩm") : "Tạm hết hàng";
                sb.append(String.format("🔹 **%s**\n   - Giá bán: **%s** (%s)\n   - 👉 <a href=\"/product/%d\" class=\"fw-bold text-primary\">Xem chi tiết sản phẩm</a>\n\n",
                        p.getName(), priceStr, stockStr, p.getId()));
            }
            sb.append("Bạn hãy bấm vào link sản phẩm để xem chi tiết cấu hình hoặc thêm vào giỏ hàng nhé!");
            return sb.toString();
        }

        if (searchMobile || lower.contains("iphone")) {
            return "Dạ hiện tại TechGear chưa có sẵn dòng **iPhone 15** trong kho. Shop đang có sẵn các dòng máy siêu phẩm khác như:\n\n"
                    + "💻 **Apple MacBook Pro 14 M3 Pro** - 45.000.000 đ\n"
                    + "🎮 **Laptop Gaming ASUS ROG Strix G16** - 28.000.000 đ\n"
                    + "🎧 **Tai nghe Bluetooth Sony WH-1000XM5** - 6.990.000 đ\n\n"
                    + "Bạn có muốn xem chi tiết các sản phẩm này không ạ?";
        }

        return "Chào bạn! Hiện tại bạn có thể hỏi mình về giá và tồn kho các sản phẩm tại TechGear (ví dụ: 'Laptop Asus', 'MacBook M3', 'Chuột Logitech', 'Tai nghe Sony') hoặc truy cập trực tiếp [Trang chủ cửa hàng](/) để chọn mua nhé!";
    }
}
