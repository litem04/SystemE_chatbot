package com.da.da.controller;

import com.da.da.entity.Customer;
import com.da.da.repository.CustomerRepository;
import com.da.da.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Date;

@Controller
public class AuthController {

    private final CustomerRepository customerRepository;
    private final CustomUserDetailsService customerService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(CustomerRepository customerRepository,
                          CustomUserDetailsService customerService,
                          PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "client/customer-register";
    }

    @PostMapping("/register")
    public String registerCustomer(@ModelAttribute Customer customer, RedirectAttributes ra) {
        try {
            Customer existingCustomer = customerRepository.findByEmail(customer.getEmail());
            if (existingCustomer != null) {
                ra.addFlashAttribute("failMessage", "Email này đã được đăng ký!");
                return "redirect:/register";
            }

            // Băm mật khẩu an toàn bằng BCrypt
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            customer.setAddedDate(new Date());
            customerRepository.save(customer);

            ra.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("failMessage", "Lỗi đăng ký: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "client/customer-login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Customer customer = customerService.findByEmail(email);

        model.addAttribute("customer", customer);
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