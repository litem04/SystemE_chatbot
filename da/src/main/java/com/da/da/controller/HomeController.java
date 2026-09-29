package com.da.da.controller;

import com.da.da.entity.Customer;
import com.da.da.entity.Product;
import com.da.da.entity.ProductReview;
import com.da.da.repository.ProductReviewRepository;
import com.da.da.service.CustomUserDetailsService;
import com.da.da.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {

    private final ProductReviewRepository productReviewRepository;
    private final CustomUserDetailsService customerService;
    private final ProductService productService;

    public HomeController(ProductReviewRepository productReviewRepository,
                          CustomUserDetailsService customerService,
                          ProductService productService) {
        this.productReviewRepository = productReviewRepository;
        this.customerService = customerService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.findAll());
        return "client/index";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String keyword, Model model) {
        List<Product> searchResults = productService.searchByName(keyword);
        model.addAttribute("products", searchResults);
        model.addAttribute("keyword", keyword);
        return "client/index";
    }

    @GetMapping("/category")
    public String filterByCategory(@RequestParam String name, Model model) {
        List<Product> categoryResults = productService.findByCategory(name);
        model.addAttribute("products", categoryResults);
        model.addAttribute("categoryName", name);
        return "client/index";
    }

    @GetMapping("/product/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        if (product != null) {
            model.addAttribute("product", product);
            List<ProductReview> reviews = productReviewRepository.findByProductId(id);
            model.addAttribute("reviews", reviews);

            List<Product> recommendedProducts = productService.getDynamicAccessories(product);
            if (recommendedProducts != null && !recommendedProducts.isEmpty()) {
                model.addAttribute("recommendedProducts", recommendedProducts);
            }

            return "client/product-detail";
        }
        return "redirect:/";
    }

    @PostMapping("/save-review")
    public String saveReview(@RequestParam("productId") Long productId,
                             @RequestParam("comment") String comment,
                             @RequestParam("rating") Integer rating,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Product product = productService.findById(productId);
        String email = principal.getName();
        Customer customer = customerService.findByEmail(email);

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