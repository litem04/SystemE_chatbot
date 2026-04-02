package com.da.da.service.tool;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

import com.da.da.entity.Product; 
import com.da.da.repository.ProductRepository;
import com.da.da.service.CartService;

@Component
public class CartTools {
	
    @Autowired
    private ProductRepository productRepository;
	
    @Autowired
    private CartService cartService;

    /**
     * Hàm bổ trợ để lấy Email người dùng đang đăng nhập
     */
    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth.getName();
    }

    @Tool("Xem danh sách sản phẩm, số lượng và tổng số tiền đang có trong giỏ hàng của người dùng hiện tại")
    public String viewMyCart() {
        String email = getCurrentUserEmail();
        if (email == null) {
            return "Người dùng chưa đăng nhập. Hãy nhắc khách hàng đăng nhập để xem giỏ hàng.";
        }
        return cartService.getCartSummaryForAi(email);
    }

    // ================= SỬA LẠI HÀM NÀY =================
    @Tool("Thêm một sản phẩm vào giỏ hàng của người dùng. Cần cung cấp productId (mã sản phẩm) và quantity (số lượng)")
    public String addProductToCart(Long productId, Integer quantity) {
        String email = getCurrentUserEmail();
        if (email == null) {
            return "Vui lòng nhắc khách hàng đăng nhập trước khi thêm sản phẩm vào giỏ hàng.";
        }

        if (quantity == null || quantity <= 0) {
            quantity = 1; 
        }

        try {
            // 1. Thêm vào giỏ hàng bằng Service
            String cartResult = cartService.addToCart(email, productId, quantity);
            
            // 2. Tạo phản hồi gửi cho AI
            StringBuilder responseToAI = new StringBuilder();
            responseToAI.append(cartResult).append("\n"); // Trả kết quả thành công trước

            // 3. Tự động lấy phụ kiện liên quan và "nhét" vào cho AI đọc
            Product mainProduct = productRepository.findById(productId).orElse(null);
            
            if (mainProduct != null && mainProduct.getName() != null) {
                String typeLower = mainProduct.getName().toLowerCase();
                String kw1 = "", kw2 = "", kw3 = "";
                
                // Bắt keyword giống trên giao diện web
                if (typeLower.contains("laptop") || typeLower.contains("máy tính")) {
                    kw1 = "chuột"; kw2 = "tai nghe"; kw3 = "balo";
                } else if (typeLower.contains("điện thoại") || typeLower.contains("phone") || typeLower.contains("iphone")) {
                    kw1 = "ốp lưng"; kw2 = "sạc"; kw3 = "tai nghe";
                }

                // Nếu là laptop/điện thoại thì mới tìm phụ kiện
                if (!kw1.isEmpty()) {
                    List<Product> accessories = productRepository.findAccessoriesForLaptop(
                            "Phụ kiện", kw1, kw2, kw3, PageRequest.of(0, 2) // Lấy top 2 phụ kiện
                    );

                    if (accessories != null && !accessories.isEmpty()) {
                        responseToAI.append("\n[CHỈ THỊ ẨN TỪ HỆ THỐNG - DÀNH RIÊNG CHO AI]:\n");
                        responseToAI.append("Hệ thống đã tìm thấy các phụ kiện có thật trong kho phù hợp với sản phẩm khách vừa mua:\n");
                        
                        for (Product p : accessories) {
                            responseToAI.append(String.format("- %s (Giá: %,.0fđ)\n", p.getName(), p.getPrice()));
                        }
                        
                        responseToAI.append("-> LỆNH BẮT BUỘC: Bạn HÃY DÙNG CHÍNH XÁC DANH SÁCH VÀ GIÁ TIỀN TRÊN để nhiệt tình mời khách hàng mua thêm. Tuyệt đối không bịa sản phẩm khác!");
                    }
                }
            }

            return responseToAI.toString();

        } catch (Exception e) {
            return "Có lỗi xảy ra khi thêm vào giỏ hàng: " + e.getMessage();
        }
    }
    // ===================================================
    
    @Tool("Tìm kiếm mã sản phẩm (productId) và tên sản phẩm dựa trên tên khách hàng cung cấp")
    public String searchProductByName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return "bồ vui lòng cung cấp tên sản phẩm để mình tìm giúp nhé.";
        }

        List<Product> products = productRepository.findByNameContainingIgnoreCase(productName);

        if (products.isEmpty()) {
            return "Rất tiếc, mình không tìm thấy sản phẩm nào có tên '" + productName + "' trong hệ thống.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Mình tìm thấy các sản phẩm sau, bồ muốn chọn cái nào:\n");
        for (Product p : products) {
            sb.append(String.format("- ID: %d | Tên: %s | Giá: %,.0fđ\n", 
                p.getId(), p.getName(), p.getPrice()));
        }
        
        return sb.toString();
    }
}