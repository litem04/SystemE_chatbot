package com.da.da.service.tool;

import com.da.da.entity.Product;
import com.da.da.repository.ProductRepository;
import com.da.da.service.CartService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Component
public class CartTools {

    private final ProductRepository productRepository;
    private final CartService cartService;

    public CartTools(ProductRepository productRepository, CartService cartService) {
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

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
            String cartResult = cartService.addToCart(email, productId, quantity);
            StringBuilder responseToAI = new StringBuilder(cartResult).append("\n");

            // Tự động tìm phụ kiện liên quan để gợi ý cross-selling
            Product mainProduct = productRepository.findById(productId).orElse(null);
            if (mainProduct != null && mainProduct.getName() != null) {
                String typeLower = mainProduct.getName().toLowerCase();
                String kw1 = "", kw2 = "", kw3 = "";

                if (typeLower.contains("laptop") || typeLower.contains("máy tính")) {
                    kw1 = "chuột"; kw2 = "tai nghe"; kw3 = "balo";
                } else if (typeLower.contains("điện thoại") || typeLower.contains("phone") || typeLower.contains("iphone")) {
                    kw1 = "ốp lưng"; kw2 = "sạc"; kw3 = "tai nghe";
                }

                if (!kw1.isEmpty()) {
                    List<Product> accessories = productRepository.findAccessoriesForLaptop(
                            "Phụ kiện", kw1, kw2, kw3, PageRequest.of(0, 2)
                    );

                    if (accessories != null && !accessories.isEmpty()) {
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        responseToAI.append("\n[GỢI Ý PHỤ KIỆN PHÙ HỢP CÓ TRONG KHO]:\n");
                        for (Product p : accessories) {
                            String priceText = p.getPrice() != null ? currencyFormat.format(p.getPrice()) : "";
                            responseToAI.append(String.format("- %s (Giá: %s)\n", p.getName(), priceText));
                        }
                        responseToAI.append("-> Bạn hãy gợi ý một cách tự nhiên danh sách phụ kiện trên cho khách hàng.");
                    }
                }
            }

            return responseToAI.toString();

        } catch (Exception e) {
            return "Có lỗi xảy ra khi thêm vào giỏ hàng: " + e.getMessage();
        }
    }

    @Tool("Tìm kiếm mã sản phẩm (productId) và tên sản phẩm dựa trên tên khách hàng cung cấp")
    public String searchProductByName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return "Bạn vui lòng cung cấp tên sản phẩm để mình hỗ trợ tìm kiếm nhé.";
        }

        List<Product> products = productRepository.findByNameContainingIgnoreCase(productName);
        if (products.isEmpty()) {
            return "Rất tiếc, cửa hàng hiện không có sản phẩm nào khớp với tên '" + productName + "'.";
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        StringBuilder sb = new StringBuilder("Dưới đây là các sản phẩm tìm thấy:\n");
        for (Product p : products) {
            String priceText = p.getPrice() != null ? currencyFormat.format(p.getPrice()) : "Liên hệ";
            sb.append(String.format("- ID: %d | Tên: %s | Giá: %s\n", p.getId(), p.getName(), priceText));
        }

        return sb.toString();
    }
}