package com.da.da.service;

import com.da.da.entity.Admin;
import com.da.da.entity.Customer;
import com.da.da.repository.AdminRepository;
import com.da.da.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private AdminRepository adminRepo;
    @Autowired private CustomerRepository customerRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm trong bảng Admin trước
        Admin admin = adminRepo.findByEmail(email);
        if (admin != null) {
            return new User(admin.getEmail(), admin.getPassword(), 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }

        // 2. Nếu không phải Admin, tìm trong bảng Customer
        Customer customer = customerRepo.findByEmail(email);
        if (customer != null) {
            return new User(customer.getEmail(), customer.getPassword(), 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }

        // 3. Không tìm thấy ai cả
        throw new UsernameNotFoundException("Tài khoản không tồn tại: " + email);
    }
    public Customer findByEmail(String email) {
        return customerRepo.findByEmail(email); // Bạn cần đảm bảo Repository có hàm này
    }

    public Customer save(Customer customer) {
        return customerRepo.save(customer);
    }

    // Logic cập nhật hồ sơ
    public void updateProfile(Customer curCustomer, Customer formCustomer) {
        // Chỉ cập nhật những trường cho phép sửa
        curCustomer.setName(formCustomer.getName());
        curCustomer.setPhone(formCustomer.getPhone());
        curCustomer.setAddress(formCustomer.getAddress());
        curCustomer.setPinCode(formCustomer.getPinCode());
        
        // Nếu có logic đổi mật khẩu thì xử lý riêng ở đây (cần mã hóa BCrypt)
        
        customerRepo.save(curCustomer);
    }
}