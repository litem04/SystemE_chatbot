package com.da.da.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.da.da.entity.Product;
import com.da.da.repository.ProductRepository;

import dev.langchain4j.agent.tool.Tool;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    // ==========================================
    // 2 HÀM MỚI THÊM ĐỂ PHỤC VỤ PRODUCT CONTROLLER
    // ==========================================
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        // Tìm sản phẩm theo ID, nếu không thấy thì trả về null tránh lỗi
        return productRepository.findById(id).orElse(null);
    }
    // ==========================================
    
    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.findByProductCategory(categoryName);
    }
    
    public String searchProductForAi(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        
        if (products.isEmpty()) {
            return "Hiện tại cửa hàng không có sản phẩm nào tên là: " + keyword;
        }

        // Biến danh sách Object thành chuỗi văn bản gọn gàng
        StringBuilder sb = new StringBuilder("Dưới đây là các sản phẩm tìm thấy:\n");
        
        boolean isLaptopSearch = false; // Biến cờ (flag) để theo dõi xem có laptop không

        for (Product p : products) {
            sb.append(String.format("- Tên: %s | Giá: %s | Kho: %d | Mô tả: %s\n", 
                p.getName(), p.getPrice(), p.getStock(), p.getDescription()));
            
            // Nếu phát hiện sản phẩm này thuộc danh mục Laptop -> Bật cờ lên true
            if (p.getProductCategory() != null && 
                p.getProductCategory().toLowerCase().contains("laptop")) {
                isLaptopSearch = true;
            }
        }

        // ============================================================
        // TÍCH HỢP AI GỢI Ý PHỤ KIỆN TỰ ĐỘNG
        // ============================================================
        if (isLaptopSearch) {
            // Lấy danh sách 4 phụ kiện từ hàm ta đã viết sẵn lúc nãy
            List<Product> accessories = getRecommendedAccessories();
            
            if (!accessories.isEmpty()) {
                // Nhét thêm "mật chú" bắt buộc con AI phải tư vấn thêm
                sb.append("\n[HƯỚNG DẪN DÀNH RIÊNG CHO AI]: Nhận thấy khách hàng đang tìm hiểu về Laptop. Sau khi tư vấn về Laptop xong, bạn BẮT BUỘC phải dùng lời lẽ tự nhiên để gợi ý khách hàng mua kèm các phụ kiện dưới đây:\n");
                
                for (Product acc : accessories) {
                    sb.append(String.format(" + Phụ kiện: %s | Giá: %s VNĐ\n", 
                        acc.getName(), acc.getPrice()));
                }
            }
        }

        return sb.toString();
    }
//    public String searchProductForAi(String keyword) {
//        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
//        
//        if (products.isEmpty()) {
//            return "Hiện tại cửa hàng không có sản phẩm nào tên là: " + keyword;
//        }
//
//        // Biến danh sách Object thành chuỗi văn bản gọn gàng
//        StringBuilder sb = new StringBuilder("Dưới đây là các sản phẩm tìm thấy:\n");
//        for (Product p : products) {
//            sb.append(String.format("- Tên: %s | Giá: %s | Kho: %d | Mô tả: %s\n", 
//                p.getName(), p.getPrice(), p.getStock(), p.getDescription()));
//        }
//        return sb.toString();
//    }
    @Tool("Sử dụng công cụ này BẤT CỨ LÚC NÀO bạn muốn lấy danh sách các phụ kiện (chuột, bàn phím, tai nghe) để chào mời, gợi ý, hoặc chèo kéo khách hàng mua thêm, ngay cả khi họ không hỏi trực tiếp.")
    public String getHotAccessoriesForSale() {
        
        List<Product> accessories = getRecommendedAccessories();
        
        if (accessories.isEmpty()) {
            return "Tạm thời hết phụ kiện để gợi ý.";
        }

        StringBuilder sb = new StringBuilder("Dưới đây là danh sách phụ kiện hot để bạn chào mời khách:\n");
        for (Product p : accessories) {
            sb.append(String.format("- %s | Giá: %s VNĐ\n", p.getName(), p.getPrice()));
        }
        sb.append("[HƯỚNG DẪN DÀNH RIÊNG CHO AI]: Hãy dùng lời lẽ khéo léo, tự nhiên để lồng ghép các sản phẩm này vào cuộc trò chuyện với khách hàng nhé.");
        
        return sb.toString();
    }
    public List<Product> getRecommendedAccessories() {
        // Sử dụng TÊN danh mục thay vì ID
        // LƯU Ý: Tên này phải giống hệt với tên danh mục phụ kiện lưu trong Database của bạn
        String categoryName = "Accessories"; 
        
        Pageable limitFour = PageRequest.of(0, 4); 
        
        return productRepository.findAccessoriesForLaptop(
                categoryName, "chuột", "phím", "tai nghe", limitFour);
    }
    public List<Product> getDynamicAccessories(Product product) {
        if (product == null || product.getProductCategory() == null || product.getName() == null) {
            return new ArrayList<>();
        }

        // Đổi hết sang chữ thường để dễ so sánh
        String categoryName = product.getProductCategory().toLowerCase();
        String productName = product.getName().toLowerCase();

        List<String> tempKeywords = new ArrayList<>();

        // 1. Nhóm Laptop / PC / Macbook
        if (categoryName.contains("laptop") || categoryName.contains("pc") || categoryName.contains("máy tính") || categoryName.contains("macbook")) {
            tempKeywords = List.of("chuột", "bàn phím", "tai nghe", "balo", "đế tản nhiệt");
        }
        // 2. Nhóm Điện thoại / Tablet / iPad (ĐÃ THÊM CHỮ "MOBILE" VÀ "TABLET")
        else if (categoryName.contains("điện") || categoryName.contains("phone") || categoryName.contains("dien thoai") || categoryName.contains("smart") || categoryName.contains("mobile") || categoryName.contains("apple") || categoryName.contains("ipad") || categoryName.contains("tablet")) {
            tempKeywords = List.of("ốp", "sạc", "dự phòng", "tai nghe", "cáp");
        }
        // 3. Nhóm Máy ảnh
        else if (categoryName.contains("camera") || categoryName.contains("máy ảnh") || productName.contains("gopro")) {
            tempKeywords = List.of("light", "thẻ nhớ", "tripod", "pin");
        }
        // 4. Nhóm Phụ kiện
        else if (categoryName.contains("accessories") || categoryName.contains("phụ kiện") || categoryName.contains("phu kien")) {
            if (productName.contains("sạc") || productName.contains("adapter")) {
                tempKeywords = List.of("cáp", "cable", "dây"); 
            } else if (productName.contains("cáp") || productName.contains("cable")) {
                tempKeywords = List.of("sạc", "adapter", "củ"); 
            } else {
                tempKeywords = List.of("balo", "túi", "giá đỡ"); 
            }
        }

        final List<String> finalKeywords = tempKeywords;
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .filter(p -> p.getId() != null && !p.getId().equals(product.getId()))
                .filter(p -> p.getName() != null)
                .filter(p -> {
                    String currentProductName = p.getName().toLowerCase();
                    String currentCategoryName = p.getProductCategory() != null ? p.getProductCategory().toLowerCase() : "";

                    // Nếu có từ khóa -> Phải ưu tiên lọc theo đúng từ khóa (Sạc, cáp, tai nghe...)
                    if (!finalKeywords.isEmpty()) {
                        return finalKeywords.stream().anyMatch(kw -> currentProductName.contains(kw));
                    }
                    
                    // Chỉ xài phao cứu sinh (bốc bừa) khi thực sự không biết sản phẩm thuộc loại gì
                    return currentCategoryName.contains("accessories") || currentCategoryName.contains("phụ kiện") || currentCategoryName.contains("phu kien");
                })
                .limit(4)
                .collect(Collectors.toList());
    }
  /*  public List<Product> getDynamicAccessories(Product mainProduct) {
        if (mainProduct == null) return List.of();

        // Lấy tên sản phẩm và tên danh mục chuyển về chữ thường để dễ soi
        String nameLower = mainProduct.getName().toLowerCase();
        String catLower = mainProduct.getProductCategory() != null ? mainProduct.getProductCategory().toLowerCase() : "";

        // Tạo sẵn 3 biến từ khóa mặc định
        String kw1 = "phụ kiện"; 
        String kw2 = "sạc"; 
        String kw3 = "cáp";

        // BẮT ĐẦU KIỂM TRA ĐỂ ĐỔI TỪ KHÓA:
        if (nameLower.contains("laptop") || nameLower.contains("macbook") || catLower.contains("laptop")) {
            // Nếu là Laptop
            kw1 = "chuột"; 
            kw2 = "tai nghe"; 
            kw3 = "balo";
        } 
        else if (nameLower.contains("điện thoại") || nameLower.contains("iphone") || nameLower.contains("samsung") || catLower.contains("phone")) {
            // Nếu là Điện thoại
            kw1 = "ốp lưng"; 
            kw2 = "sạc"; 
            kw3 = "tai nghe";
        }

        // Tên danh mục phụ kiện trong DB của bồ đang là "Accessories"
        String categoryName = "Accessories"; 
        
        // Gọi thẳng vào Repository, lấy đúng 4 sản phẩm
        return productRepository.findAccessoriesForLaptop(
                categoryName, kw1, kw2, kw3, PageRequest.of(0, 4)
        );
    }*/
}