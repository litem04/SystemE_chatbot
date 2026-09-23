package com.da.da.service.tool;

import com.da.da.entity.Product;
import com.da.da.repository.ProductRepository;
import com.da.da.service.ProductService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Component
public class ProductTools {

    private final ProductService productService;
    private final ProductRepository productRepository;

    public ProductTools(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Tool("Tìm kiếm thông tin sản phẩm từ cơ sở dữ liệu theo tên hoặc danh mục")
    public String searchProduct(String keyword) {
        return productService.searchProductForAi(keyword);
    }

    @Tool("Lấy danh sách phụ kiện gợi ý (cross-selling) cho khách hàng dựa trên sản phẩm chính (laptop, điện thoại)")
    public String getCrossSellRecommendations(String mainProductType) {
        if (mainProductType == null || mainProductType.trim().isEmpty()) {
            return "Không có thông tin sản phẩm chính để gợi ý.";
        }

        String categoryName = "Phụ kiện";
        String kw1 = "";
        String kw2 = "";
        String kw3 = "";

        String typeLower = mainProductType.toLowerCase();
        if (typeLower.contains("laptop") || typeLower.contains("máy tính")) {
            kw1 = "chuột";
            kw2 = "balo";
            kw3 = "bàn phím";
        } else if (typeLower.contains("điện thoại") || typeLower.contains("phone")) {
            kw1 = "ốp lưng";
            kw2 = "sạc";
            kw3 = "tai nghe";
        } else {
            return "Hiện tại hệ thống hỗ trợ gợi ý phụ kiện cho Laptop và Điện thoại.";
        }

        Pageable topThree = PageRequest.of(0, 3);
        List<Product> recommendations = productRepository.findAccessoriesForLaptop(categoryName, kw1, kw2, kw3, topThree);

        if (recommendations == null || recommendations.isEmpty()) {
            return "Hiện không có sẵn phụ kiện phù hợp trong kho để gợi ý.";
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        StringBuilder sb = new StringBuilder();
        sb.append("Danh sách phụ kiện gợi ý liên quan đến ").append(mainProductType).append(":\n");
        for (Product p : recommendations) {
            String priceText = p.getPrice() != null ? currencyFormat.format(p.getPrice()) : "Liên hệ";
            sb.append(String.format("- %s (Mã ID: %d) | Giá: %s\n", p.getName(), p.getId(), priceText));
        }
        sb.append("\n(Lưu ý cho AI: Hãy khéo léo dùng thông tin này để gợi ý khách hàng mua thêm).");

        return sb.toString();
    }
}