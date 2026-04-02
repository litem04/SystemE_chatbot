package com.da.da.service.tool;


import com.da.da.entity.Product;
import com.da.da.repository.ProductRepository;
import com.da.da.service.ProductService;

import dev.langchain4j.agent.tool.Tool;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductTools {

    @Autowired
    private ProductService productService; // Hoặc ProductRepository
    @Autowired
    private ProductRepository productRepository;
    
    @Tool("Tìm kiếm thông tin sản phẩm từ database theo tên hoặc loại")
    public String searchProduct(String keyword) {
        // AI sẽ truyền 'keyword' vào đây, mình gọi Service xử lý
        return productService.searchProductForAi(keyword);
    }
    
    @Tool("Lấy danh sách các phụ kiện gợi ý (cross-selling) cho khách hàng dựa trên sản phẩm chính (ví dụ: laptop, điện thoại) mà họ vừa hỏi mua hoặc thêm vào giỏ hàng.")
    public String getCrossSellRecommendations(String mainProductType) {
        if (mainProductType == null || mainProductType.trim().isEmpty()) {
            return "Không có thông tin sản phẩm chính để gợi ý.";
        }

        String categoryName = "Phụ kiện"; // Đảm bảo chuỗi này khớp với category trong DB của bạn
        String kw1 = "";
        String kw2 = "";
        String kw3 = "";

        // Phân loại từ khóa dựa trên sản phẩm chính AI truyền vào
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
            return "Hiện tại hệ thống chỉ có gợi ý phụ kiện cho Laptop và Điện thoại.";
        }

        // Lấy top 3 sản phẩm phụ kiện phù hợp
        Pageable topThree = PageRequest.of(0, 3);
        List<Product> recommendations = productRepository.findAccessoriesForLaptop(categoryName, kw1, kw2, kw3, topThree);

        if (recommendations == null || recommendations.isEmpty()) {
            return "Hiện không có sẵn phụ kiện phù hợp trong kho để gợi ý.";
        }

        // Tạo chuỗi kết quả trả về cho LLM
        StringBuilder sb = new StringBuilder();
        sb.append("Danh sách phụ kiện gợi ý liên quan đến ").append(mainProductType).append(":\n");
        for (Product p : recommendations) {
            sb.append(String.format("- %s (Mã ID: %d) | Giá: %,.0fđ\n", p.getName(), p.getId(), p.getPrice()));
        }
        sb.append("\n(Lưu ý cho AI: Hãy khéo léo dùng thông tin này để mời khách hàng mua thêm).");
        
        return sb.toString();
    }
    
    
}