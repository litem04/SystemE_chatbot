package com.da.da.controller;

import com.da.da.entity.Cart;
import com.da.da.entity.Customer;
import com.da.da.repository.CartRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CartController {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public CartController(CartRepository cartRepository,
                          ProductRepository productRepository,
                          CartService cartService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Cart> cartItems = cartRepository.findByCustomerIdOrderByIdAsc(Long.valueOf(user.getId()));
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Cart item : cartItems) {
            if (item.getProduct() != null) {
                BigDecimal correctTotal = cartService.calculateMixedTotal(item.getProduct(), item.getQuantity() != null ? item.getQuantity() : 1);
                grandTotal = grandTotal.add(correctTotal);
                item.setTotalPrice(correctTotal);
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", grandTotal);
        return "client/cart";
    }

    @RequestMapping(value = "/cart/add", method = {RequestMethod.GET, RequestMethod.POST})
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

    @GetMapping("/cart/remove/{productId}")
    public String removeItem(@PathVariable Long productId, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        if (customer != null) {
            cartRepository.deleteByCustomerIdAndProductId(Long.valueOf(customer.getId()), productId);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove")
    public String removeItemById(@RequestParam Long id) {
        cartRepository.deleteById(id);
        return "redirect:/cart";
    }

    @GetMapping("/cart/update/{id}")
    public String updateCartGet(@PathVariable("id") Long productId,
                                @RequestParam("qty") int qty,
                                HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        if (customer != null) {
            cartService.updateCartQuantity(customer.getEmail(), productId, qty);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCartPost(@RequestParam Long id, @RequestParam int quantity, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        if (customer != null) {
            Cart cartItem = cartRepository.findById(id).orElse(null);
            if (cartItem != null && cartItem.getProduct() != null) {
                cartService.updateCartQuantity(customer.getEmail(), cartItem.getProduct().getId(), quantity);
            }
        }
        return "redirect:/cart";
    }
}