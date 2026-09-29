package com.da.da.controller;

import com.da.da.dto.RegisterRequest;
import com.da.da.entity.Customer;
import com.da.da.service.AuthService;
import com.da.da.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthController {

    private final AuthService authService;
    private final CustomUserDetailsService customerService;

    public AuthController(AuthService authService,
                          CustomUserDetailsService customerService) {
        this.authService = authService;
        this.customerService = customerService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("customer", new RegisterRequest());
        return "client/customer-register";
    }

    @PostMapping("/register")
    public String registerCustomer(@Valid @ModelAttribute("customer") RegisterRequest request,
                                   BindingResult bindingResult,
                                   RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            ra.addFlashAttribute("failMessage", errorMsg);
            return "redirect:/register";
        }

        try {
            authService.register(request);
            ra.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("failMessage", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            ra.addFlashAttribute("failMessage", "Lỗi đăng ký: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "client/customer-login";
    }



    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Customer customer = customerService.findByEmail(email);

        Customer safeCustomer = Customer.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .name(customer.getName())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .gender(customer.getGender())
                .pinCode(customer.getPinCode())
                .addedDate(customer.getAddedDate())
                .password(null)
                .build();

        model.addAttribute("customer", safeCustomer);
        model.addAttribute("title", "Hồ sơ cá nhân");
        return "client/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute("customer") Customer formCustomer,
                                Principal principal,
                                Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Customer currentCustomer = customerService.findByEmail(email);

        customerService.updateProfile(currentCustomer, formCustomer);

        model.addAttribute("message", "Cập nhật thông tin thành công!");
        model.addAttribute("customer", currentCustomer);
        return "client/profile";
    }
}