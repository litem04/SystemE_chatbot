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
import org.springframework.web.bind.annotation.RequestParam;
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
        if (!model.containsAttribute("customer")) {
            RegisterRequest request = new RegisterRequest();
            request.setGender("Nam");
            model.addAttribute("customer", request);
        }
        return "client/customer-register";
    }

    @PostMapping("/register")
    public String registerCustomer(@Valid @ModelAttribute("customer") RegisterRequest request,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "client/customer-register";
        }

        try {
            authService.register(request);
            ra.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Dữ liệu không hợp lệ.";
            if (msg.toLowerCase().contains("email")) {
                bindingResult.rejectValue("email", "duplicate.email", msg);
            } else if (msg.toLowerCase().contains("thoại") || msg.toLowerCase().contains("phone")) {
                bindingResult.rejectValue("phone", "invalid.phone", msg);
            } else {
                model.addAttribute("failMessage", msg);
            }
            return "client/customer-register";
        } catch (Exception e) {
            model.addAttribute("failMessage", "Lỗi đăng ký: " + e.getMessage());
            return "client/customer-register";
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
        if (customer == null) {
            return "redirect:/login";
        }

        com.da.da.dto.UpdateProfileRequest profileRequest = com.da.da.dto.UpdateProfileRequest.builder()
                .email(customer.getEmail())
                .name(customer.getName())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .gender(customer.getGender())
                .pinCode(customer.getPinCode())
                .build();

        model.addAttribute("customer", profileRequest);
        model.addAttribute("title", "Hồ sơ cá nhân");
        return "client/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@Valid @ModelAttribute("customer") com.da.da.dto.UpdateProfileRequest formRequest,
                                BindingResult bindingResult,
                                Principal principal,
                                Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("failMessage", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("customer", formRequest);
            return "client/profile";
        }

        String email = principal.getName();
        Customer currentCustomer = customerService.findByEmail(email);
        if (currentCustomer == null) {
            return "redirect:/login";
        }

        customerService.updateProfile(currentCustomer, formRequest);

        model.addAttribute("message", "Cập nhật thông tin thành công!");
        model.addAttribute("customer", formRequest);
        return "client/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes ra) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            authService.changePassword(principal.getName(), oldPassword, newPassword, confirmPassword);
            ra.addFlashAttribute("pwdSuccess", "Đổi mật khẩu thành công!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("pwdError", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("pwdError", "Lỗi đổi mật khẩu: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}