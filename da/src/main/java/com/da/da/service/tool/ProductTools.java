package com.da.da.service.tool;


import com.da.da.service.ProductService;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductTools {

    @Autowired
    private ProductService productService; // Hoặc ProductRepository

    @Tool("Tìm kiếm thông tin sản phẩm từ database theo tên hoặc loại")
    public String searchProduct(String keyword) {
        // AI sẽ truyền 'keyword' vào đây, mình gọi Service xử lý
        return productService.searchProductForAi(keyword);
    }
    
}