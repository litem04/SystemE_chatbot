package com.da.da.service.tool;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CartService cartService; // Đổi từ OrderService sang CartService

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

        // Gọi hàm summary đã viết trong CartService
        return cartService.getCartSummaryForAi(email);
    }

    @Tool("Thêm một sản phẩm vào giỏ hàng của người dùng. Cần cung cấp productId (mã sản phẩm) và quantity (số lượng)")
    public String addProductToCart(Long productId, Integer quantity) {
        String email = getCurrentUserEmail();
        if (email == null) {
            return "Vui lòng nhắc khách hàng đăng nhập trước khi thêm sản phẩm vào giỏ hàng.";
        }

        if (quantity == null || quantity <= 0) {
            quantity = 1; // Mặc định thêm 1 nếu AI không xác định được số lượng
        }

        // Gọi hàm addToCart trong CartService
        try {
            return cartService.addToCart(email, productId, quantity);
        } catch (Exception e) {
            return "Có lỗi xảy ra khi thêm vào giỏ hàng: " + e.getMessage();
        }
    }
    
    @Tool("Tìm kiếm mã sản phẩm (productId) và tên sản phẩm dựa trên tên khách hàng cung cấp")
    public String searchProductByName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return "bồ vui lòng cung cấp tên sản phẩm để mình tìm giúp nhé.";
        }

        // Gọi Repository để tìm sản phẩm theo tên
        List<Product> products = productRepository.findByNameContainingIgnoreCase(productName);

        if (products.isEmpty()) {
            return "Rất tiếc, mình không tìm thấy sản phẩm nào có tên '" + productName + "' trong hệ thống.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Mình tìm thấy các sản phẩm sau, bồ muốn chọn cái nào:\n");
        for (Product p : products) {
            // Trả về ID và Tên để AI biết đường mà gọi hàm AddToCart
            sb.append(String.format("- ID: %d | Tên: %s | Giá: %,.0fđ\n", 
                p.getId(), p.getName(), p.getPrice()));
        }
        
        return sb.toString();
    }
}