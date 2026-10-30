package com.techgear.identity.service;

import com.techgear.identity.dto.RegisterRequest;
import com.techgear.identity.entity.Customer;
import com.techgear.identity.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Customer register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu đăng ký không hợp lệ.");
        }

        Customer existingCustomer = customerRepository.findByEmail(request.getEmail().trim().toLowerCase());
        if (existingCustomer != null) {
            throw new IllegalArgumentException("Email này đã được đăng ký!");
        }

        String phone = request.getPhone();
        if (phone != null && !phone.isBlank() && !phone.matches("^(0|\\+84)[0-9]{9,10}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (cần 10 số, bắt đầu bằng 0 hoặc +84).");
        }

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(phone != null ? phone.trim() : "")
                .gender(normalizeGender(request.getGender()))
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .pinCode(request.getPinCode())
                .addedDate(new Date())
                .build();

        return customerRepository.save(customer);
    }

    public static String normalizeGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return "Nam";
        }
        String g = gender.trim();
        if ("male".equalsIgnoreCase(g) || "nam".equalsIgnoreCase(g)) {
            return "Nam";
        }
        if ("female".equalsIgnoreCase(g) || "nu".equalsIgnoreCase(g) || "nữ".equalsIgnoreCase(g)) {
            return "Nữ";
        }
        return g;
    }

    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Người dùng chưa đăng nhập.");
        }
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu hiện tại.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu mới không trùng khớp.");
        }
        Customer customer = customerRepository.findByEmail(email.trim().toLowerCase());
        if (customer == null) {
            throw new IllegalArgumentException("Tài khoản không tồn tại.");
        }
        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác.");
        }
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }
}
