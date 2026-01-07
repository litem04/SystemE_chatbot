package com.da.da.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.da.da.entity.Product;
import com.da.da.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public String searchProductForAi(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        
        if (products.isEmpty()) {
            return "Hiện tại cửa hàng không có sản phẩm nào tên là: " + keyword;
        }

        // Biến danh sách Object thành chuỗi văn bản gọn gàng
        StringBuilder sb = new StringBuilder("Dưới đây là các sản phẩm tìm thấy:\n");
        for (Product p : products) {
            sb.append(String.format("- Tên: %s | Giá: %s | Kho: %d | Mô tả: %s\n", 
                p.getName(), p.getPrice(), p.getStock(), p.getDescription()));
        }
        return sb.toString();
    }
}