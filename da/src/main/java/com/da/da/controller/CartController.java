package com.da.da.controller;

import com.da.da.entity.Customer;
import com.da.da.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        CartService.CartViewDto cartView = cartService.getCartView(Long.valueOf(user.getId()));
        model.addAttribute("cartItems", cartView.cartItems());
        model.addAttribute("grandTotal", cartView.grandTotal());
        return "client/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session,
                            RedirectAttributes ra) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        String result = cartService.addToCart(user.getEmail(), productId, quantity != null ? quantity : 1);
        if (result.startsWith("Lỗi") || result.startsWith("Rất tiếc")) {
            ra.addFlashAttribute("errorMessage", result);
        } else {
            ra.addFlashAttribute("successMessage", result);
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeItem(@PathVariable Long productId, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        if (customer != null) {
            cartService.removeItem(Long.valueOf(customer.getId()), productId);
        }
        return "redirect:/cart";
    }

    // Chống IDOR: Chỉ xóa bản ghi giỏ hàng thuộc về đúng người dùng đang đăng nhập
    @PostMapping("/cart/remove")
    public String removeItemById(@RequestParam Long id, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        if (customer != null) {
            cartService.removeItemById(Long.valueOf(customer.getId()), id);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCartGet(@PathVariable("id") Long productId,
                                @RequestParam("qty") int qty,
                                HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        if (customer != null) {
            cartService.updateCartQuantity(customer.getEmail(), productId, qty);
        }
        return "redirect:/cart";
    }

    // Chống IDOR: Chỉ cập nhật bản ghi giỏ hàng thuộc về đúng người dùng đang đăng nhập
    @PostMapping("/cart/update")
    public String updateCartPost(@RequestParam Long id, @RequestParam int quantity, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        if (customer != null) {
            cartService.updateCartItemQuantity(Long.valueOf(customer.getId()), id, quantity);
        }
        return "redirect:/cart";
    }
}