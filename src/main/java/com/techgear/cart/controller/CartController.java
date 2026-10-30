package com.techgear.cart.controller;

import com.techgear.identity.entity.Customer;
import com.techgear.cart.service.CartService;
import com.techgear.identity.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class CartController {

    private final CartService cartService;
    private final CustomUserDetailsService customerService;

    public CartController(CartService cartService, CustomUserDetailsService customerService) {
        this.cartService = cartService;
        this.customerService = customerService;
    }

    private Customer getActiveUser(HttpSession session, Principal principal) {
        Customer user = (Customer) session.getAttribute("user");
        if (user != null) return user;
        if (principal != null) {
            user = customerService.getActiveCustomer(principal.getName());
            if (user != null) {
                session.setAttribute("user", user);
            }
        }
        return user;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model, Principal principal) {
        Customer user = getActiveUser(session, principal);
        if (user == null) {
            if (session.getAttribute("admin") != null) {
                model.addAttribute("cartItems", java.util.Collections.emptyList());
                model.addAttribute("grandTotal", java.math.BigDecimal.ZERO);
                model.addAttribute("infoMessage", "Bạn đang đăng nhập với tư cách Quản trị viên (Admin). Giỏ hàng chỉ khả dụng cho tài khoản Khách hàng.");
                return "client/cart";
            }
            return "redirect:/login";
        }

        CartService.CartViewDto cartView = cartService.getCartView(Long.valueOf(user.getId()));
        model.addAttribute("cartItems", cartView.cartItems());
        model.addAttribute("grandTotal", cartView.grandTotal());
        return "client/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session,
                            Principal principal,
                            RedirectAttributes ra) {
        Customer user = getActiveUser(session, principal);
        if (user == null) {
            if (session.getAttribute("admin") != null) {
                ra.addFlashAttribute("errorMessage", "Tài khoản Quản trị viên không thể mua hàng. Vui lòng đăng nhập bằng tài khoản Khách hàng.");
                return "redirect:/product-detail/" + productId;
            }
            return "redirect:/login";
        }

        String result = cartService.addToCart(user.getEmail(), productId, quantity != null ? quantity : 1);
        if (result.startsWith("Lỗi") || result.startsWith("Rất tiếc")) {
            ra.addFlashAttribute("errorMessage", result);
        } else {
            ra.addFlashAttribute("successMessage", result);
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeItem(@PathVariable Long productId, HttpSession session, Principal principal) {
        Customer customer = getActiveUser(session, principal);
        if (customer != null) {
            cartService.removeItem(Long.valueOf(customer.getId()), productId);
        }
        return "redirect:/cart";
    }

    // Chống IDOR: Chỉ xóa bản ghi giỏ hàng thuộc về đúng người dùng đang đăng nhập
    @PostMapping("/cart/remove")
    public String removeItemById(@RequestParam Long id, HttpSession session, Principal principal) {
        Customer customer = getActiveUser(session, principal);
        if (customer != null) {
            cartService.removeItemById(Long.valueOf(customer.getId()), id);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCartGet(@PathVariable("id") Long productId,
                                @RequestParam("qty") int qty,
                                HttpSession session,
                                Principal principal) {
        Customer customer = getActiveUser(session, principal);
        if (customer != null) {
            cartService.updateCartQuantity(customer.getEmail(), productId, qty);
        }
        return "redirect:/cart";
    }

    // Chống IDOR: Chỉ cập nhật bản ghi giỏ hàng thuộc về đúng người dùng đang đăng nhập
    @PostMapping("/cart/update")
    public String updateCartPost(@RequestParam Long id, @RequestParam int quantity, HttpSession session, Principal principal) {
        Customer customer = getActiveUser(session, principal);
        if (customer != null) {
            cartService.updateCartItemQuantity(Long.valueOf(customer.getId()), id, quantity);
        }
        return "redirect:/cart";
    }
}