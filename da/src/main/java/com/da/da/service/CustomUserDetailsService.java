package com.da.da.service;

import com.da.da.entity.Admin;
import com.da.da.entity.Customer;
import com.da.da.repository.AdminRepository;
import com.da.da.repository.CustomerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepo;
    private final CustomerRepository customerRepo;

    public CustomUserDetailsService(AdminRepository adminRepo, CustomerRepository customerRepo) {
        this.adminRepo = adminRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepo.findByEmail(email);
        if (admin != null) {
            return new User(admin.getEmail(), admin.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }

        Customer customer = customerRepo.findByEmail(email);
        if (customer != null) {
            return new User(customer.getEmail(), customer.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }

        throw new UsernameNotFoundException("Tài khoản không tồn tại: " + email);
    }

    public Customer findByEmail(String email) {
        return customerRepo.findByEmail(email);
    }

    @Transactional
    public Customer save(Customer customer) {
        return customerRepo.save(customer);
    }

    @Transactional
    public void updateProfile(Customer curCustomer, Customer formCustomer) {
        if (curCustomer == null || formCustomer == null) return;
        curCustomer.setName(formCustomer.getName());
        curCustomer.setPhone(formCustomer.getPhone());
        curCustomer.setAddress(formCustomer.getAddress());
        curCustomer.setPinCode(formCustomer.getPinCode());
        customerRepo.save(curCustomer);
    }
}