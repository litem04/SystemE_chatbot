package com.da.da.controller;

import com.da.da.entity.Customer;
import com.da.da.entity.Product;
import com.da.da.entity.ProductReview;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.repository.ProductReviewRepository;
import com.da.da.service.CustomUserDetailsService;
import com.da.da.service.ProductService;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductReviewRepository productReviewRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductService productService;
    @GetMapping("/")
    public String home(Model model) {
    
        // Lấy tất cả sản phẩm Active (đang bán)
        // Lưu ý: Nếu muốn lọc chỉ lấy Active thì viết thêm hàm trong Repo, tạm thời lấy hết
        model.addAttribute("products", productRepository.findAll());
        return "client/index"; // Trả về file index.html trong thư mục client
    }
 // 2. CHỨC NĂNG TÌM KIẾM (Search)
    // Link gọi: /search?keyword=iphone
    @GetMapping("/search")
    public String searchProduct(@RequestParam String keyword, Model model) {
        // Gọi hàm tìm kiếm theo TÊN trong Repository
        List<Product> searchResults = productRepository.findByNameContainingIgnoreCase(keyword);
        
        model.addAttribute("products", searchResults);
        model.addAttribute("keyword", keyword); // Để hiển thị lại từ khóa user vừa nhập
        return "client/index"; // Dùng lại trang index để hiện kết quả
    }

    // 3. CHỨC NĂNG LỌC DANH MỤC (Category)
    // Link gọi: /category?name=Mobile
    @GetMapping("/category")
    public String filterByCategory(@RequestParam String name, Model model) {
        // Gọi hàm tìm kiếm theo DANH MỤC trong Repository
        List<Product> categoryResults = productRepository.findByProductCategory(name);
        
        model.addAttribute("products", categoryResults);
        model.addAttribute("categoryName", name); // Để hiện tiêu đề "Danh mục: Mobile"
        return "client/index"; // Dùng lại trang index
    }
 // --- 3. Sửa lại hàm viewProductDetails ---
    @GetMapping("/product/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        
        if (product != null) {
            // 1. Gửi thông tin sản phẩm
            model.addAttribute("product", product);
            
            // 2. Lấy danh sách review của sản phẩm này
            List<ProductReview> reviews = productReviewRepository.findByProductId(id);
            model.addAttribute("reviews", reviews);

            // ============================================================
            // 3. [CẬP NHẬT] Lấy danh sách phụ kiện ĐỘNG cho MỌI LOẠI SẢN PHẨM
            // ============================================================
            // Gọi hàm getDynamicAccessories (hàm này đã tự động check if-else Điện thoại/Laptop bên trong nó rồi)
            List<Product> recommendedProducts = productService.getDynamicAccessories(product);
            
            // Nếu tìm thấy phụ kiện thì ném sang cho HTML hiển thị
            if (recommendedProducts != null && !recommendedProducts.isEmpty()) {
                model.addAttribute("recommendedProducts", recommendedProducts);
            }

            // Trả về giao diện
            return "client/product-detail"; 
        }
        return "redirect:/";
    }
//    @GetMapping("/product/{id}")
//    public String viewProductDetails(@PathVariable Long id, Model model) {
//        Product product = productRepository.findById(id).orElse(null);
//        if (product != null) {
//            model.addAttribute("product", product);
//            
//            // [MỚI] Lấy danh sách review của sản phẩm này
//            List<ProductReview> reviews = productReviewRepository.findByProductId(id);
//            model.addAttribute("reviews", reviews);
//
//            return "client/product-detail"; 
//        }
//        return "redirect:/";
//    }

    @PostMapping("/save-review")
    public String saveReview(@RequestParam("productId") Long productId,
                             @RequestParam("comment") String comment,
                             @RequestParam("rating") Integer rating,
                             Principal principal) {
        
        if (principal == null) {
            return "redirect:/login";
        }

        Product product = productRepository.findById(productId).orElse(null);
        
        // --- SỬA ĐOẠN NÀY ---
        String email = principal.getName();
        // Gọi repository để tìm Customer theo email
        Customer customer = customerRepository.findByEmail(email); 
        // Lưu ý: Bạn cần chắc chắn trong CustomerRepository đã có hàm findByEmail
        
        if (product != null && customer != null) {
            ProductReview review = new ProductReview();
            review.setProduct(product);
            review.setCustomer(customer);
            review.setComment(comment);
            review.setRating(rating);
            review.setDate(new Date());

            productReviewRepository.save(review);
        }

        return "redirect:/product/" + productId;
    }
   
    
}