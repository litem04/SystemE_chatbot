package com.da.da.controller;

import com.da.da.entity.Product;
import com.da.da.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 1. Hiển thị trang danh sách tất cả sản phẩm
     * Đường dẫn truy cập trên web: http://localhost:8081/products
     */
    @GetMapping("/products")
    public String listProducts(Model model) {
        // Lấy tất cả sản phẩm từ Database (Bạn cần đảm bảo productService có hàm findAll() nhé)
        List<Product> products = productService.findAll(); 
        
        // Ném danh sách này sang giao diện với tên biến là "products"
        model.addAttribute("products", products);
        
        // Trả về giao diện tên là products.html trong thư mục templates
        return "products"; 
    }

    /**
     * 2. Hiển thị trang Chi tiết của 1 sản phẩm cụ thể
     * Đường dẫn truy cập trên web ví dụ: http://localhost:8081/product/1
     */
    
    @GetMapping("/product-detail") // ĐỂ ĐƯỜNG DẪN TĨNH, KHÔNG CÓ NGOẶC NHỌN
    public String productDetail(@RequestParam("id") Long id, Model model) {
        
        Product product = productService.findById(id);
        
        if (product == null) {
            return "redirect:/products"; 
        }

        // Logic "ĐỘNG" tìm phụ kiện vẫn giữ nguyên, rất ngon lành
        List<Product> accessories = productService.getDynamicAccessories(product);

        model.addAttribute("product", product);
        model.addAttribute("accessories", accessories);
        
        return "product-detail"; 
    }

}