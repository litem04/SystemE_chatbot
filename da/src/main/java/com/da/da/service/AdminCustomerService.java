package com.da.da.service;

import com.da.da.entity.Customer;
import com.da.da.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminCustomerService {

    private static final Logger log = LoggerFactory.getLogger(AdminCustomerService.class);

    private final CustomerRepository customerRepository;
    private final SessionRegistry sessionRegistry;

    public AdminCustomerService(CustomerRepository customerRepository, SessionRegistry sessionRegistry) {
        this.customerRepository = customerRepository;
        this.sessionRegistry = sessionRegistry;
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public long countCustomers() {
        return customerRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return customerRepository.searchCustomer(keyword.trim());
        }
        return customerRepository.findAll();
    }

    @Transactional
    public void deleteCustomerAndExpireSessions(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng ID: " + customerId));

        String email = customer.getEmail();
        log.info("Xóa tài khoản khách hàng ID: {}, Email: {}. Đang thu hồi các phiên đăng nhập...", customerId, email);

        List<Object> principals = sessionRegistry.getAllPrincipals();
        for (Object principal : principals) {
            if (principal instanceof UserDetails user) {
                if (user.getUsername().equalsIgnoreCase(email)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    for (SessionInformation sessionInfo : sessions) {
                        sessionInfo.expireNow();
                        log.info("Đã hủy session: {}", sessionInfo.getSessionId());
                    }
                }
            }
        }

        customerRepository.deleteById(customerId);
        log.info("Đã xóa hoàn tất khách hàng ID: {}", customerId);
    }
}
