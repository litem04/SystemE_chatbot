package com.da.da.service;

import com.da.da.dto.RegisterRequest;
import com.da.da.entity.Customer;
import com.da.da.repository.CustomerRepository;
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

        String phone = request.getEffectiveMobileNumber();
        if (phone != null && !phone.isBlank() && !phone.matches("^(0|\\+84)[0-9]{9,10}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (cần 10 số, bắt đầu bằng 0 hoặc +84).");
        }

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(phone != null ? phone.trim() : "")
                .gender(request.getGender())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .pinCode(request.getEffectivePincode())
                .addedDate(new Date())
                .build();

        return customerRepository.save(customer);
    }
}
