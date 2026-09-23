package com.da.da.service;

import com.da.da.entity.Product;
import com.da.da.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.findByProductCategory(categoryName);
    }

    @Transactional(readOnly = true)
    public String searchProductForAi(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        if (products.isEmpty()) {
            return "Hiện tại cửa hàng không có sản phẩm nào khớp với từ khóa: " + keyword;
        }

        StringBuilder sb = new StringBuilder("Dưới đây là các sản phẩm tìm thấy:\n");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        boolean isLaptopSearch = false;

        for (Product p : products) {
            String priceText = p.getPrice() != null ? currencyFormat.format(p.getPrice()) : "Liên hệ";
            sb.append(String.format("- ID: %d | Tên: %s | Giá: %s | Tồn kho: %d | Mô tả: %s\n",
                    p.getId(), p.getName(), priceText, p.getStock() != null ? p.getStock() : 0, p.getDescription()));

            if (p.getProductCategory() != null && p.getProductCategory().toLowerCase().contains("laptop")) {
                isLaptopSearch = true;
            }
        }

        if (isLaptopSearch) {
            List<Product> accessories = getRecommendedAccessories();
            if (!accessories.isEmpty()) {
                sb.append("\n[GỢI Ý PHỤ KIỆN KÈM THEO]:\n");
                for (Product acc : accessories) {
                    String accPrice = acc.getPrice() != null ? currencyFormat.format(acc.getPrice()) : "";
                    sb.append(String.format(" + Phụ kiện: %s | Giá: %s\n", acc.getName(), accPrice));
                }
            }
        }

        return sb.toString();
    }

    @Transactional(readOnly = true)
    public List<Product> getRecommendedAccessories() {
        String categoryName = "Accessories";
        Pageable limitFour = PageRequest.of(0, 4);
        return productRepository.findAccessoriesForLaptop(categoryName, "chuột", "phím", "tai nghe", limitFour);
    }

    @Transactional(readOnly = true)
    public List<Product> getDynamicAccessories(Product product) {
        if (product == null || product.getProductCategory() == null || product.getName() == null) {
            return new ArrayList<>();
        }

        String categoryName = product.getProductCategory().toLowerCase();
        String productName = product.getName().toLowerCase();
        List<String> tempKeywords = new ArrayList<>();

        if (categoryName.contains("laptop") || categoryName.contains("pc") || categoryName.contains("máy tính") || categoryName.contains("macbook")) {
            tempKeywords = List.of("chuột", "bàn phím", "tai nghe", "balo", "đế tản nhiệt");
        } else if (categoryName.contains("điện") || categoryName.contains("phone") || categoryName.contains("dien thoai") || categoryName.contains("smart") || categoryName.contains("mobile") || categoryName.contains("apple") || categoryName.contains("ipad") || categoryName.contains("tablet")) {
            tempKeywords = List.of("ốp", "sạc", "dự phòng", "tai nghe", "cáp");
        } else if (categoryName.contains("camera") || categoryName.contains("máy ảnh") || productName.contains("gopro")) {
            tempKeywords = List.of("light", "thẻ nhớ", "tripod", "pin");
        } else {
            tempKeywords = List.of("chuột", "sạc", "tai nghe");
        }

        final List<String> finalKeywords = tempKeywords;
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .filter(p -> p.getId() != null && !p.getId().equals(product.getId()))
                .filter(p -> p.getName() != null)
                .filter(p -> {
                    String currentProductName = p.getName().toLowerCase();
                    return finalKeywords.stream().anyMatch(currentProductName::contains);
                })
                .limit(4)
                .collect(Collectors.toList());
    }
}