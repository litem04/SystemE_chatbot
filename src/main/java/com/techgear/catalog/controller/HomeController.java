package com.techgear.catalog.controller;

import com.techgear.catalog.entity.Product;
import com.techgear.review.entity.ProductReview;
import com.techgear.catalog.service.ProductService;
import com.techgear.review.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    private final ReviewService reviewService;
    private final ProductService productService;

    public HomeController(ReviewService reviewService,
                          ProductService productService) {
        this.reviewService = reviewService;
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
    public String viewProductDetails(@PathVariable Long id, Model model, Principal principal) {
        Product product = productService.findById(id);
        if (product != null) {
            model.addAttribute("product", product);

            List<ProductReview> reviews = reviewService.getReviewsForProduct(id);
            model.addAttribute("reviews", reviews);
            model.addAttribute("avgRating", reviewService.getAverageRating(id));
            model.addAttribute("reviewCount", reviewService.getReviewCount(id));

            boolean hasPurchased = false;
            if (principal != null) {
                hasPurchased = reviewService.hasUserPurchased(principal.getName(), id);
            }
            model.addAttribute("hasPurchased", hasPurchased);
            model.addAttribute("isLoggedIn", principal != null);

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
                             Principal principal,
                             RedirectAttributes ra) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            reviewService.saveReview(productId, principal.getName(), comment, rating);
            ra.addFlashAttribute("reviewSuccess", "Cảm ơn bạn đã gửi đánh giá sản phẩm!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("reviewError", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("reviewError", "Không thể gửi đánh giá: " + e.getMessage());
        }

        return "redirect:/product/" + productId;
    }
}