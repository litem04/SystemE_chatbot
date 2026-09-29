package com.da.da.config;

import com.da.da.entity.Admin;
import com.da.da.entity.Customer;
import com.da.da.repository.AdminRepository;
import com.da.da.repository.CustomerRepository;
import com.da.da.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final AdminRepository adminRepo;
    private final CustomerRepository customerRepo;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          AdminRepository adminRepo,
                          CustomerRepository customerRepo) {
        this.customUserDetailsService = customUserDetailsService;
        this.adminRepo = adminRepo;
        this.customerRepo = customerRepo;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(customUserDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/webhook/**")
            )
            .authorizeHttpRequests(auth -> auth
                // 1. Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/product-images/**", "/webjars/**", "/favicon.ico").permitAll()
                // 2. Public view pages & Search
                .requestMatchers("/", "/login", "/register", "/search", "/product/**", "/products/**", "/order-success").permitAll()
                // 3. Public APIs (Chatbot, Translate, Webhook)
                .requestMatchers("/api/webhook/**", "/api/ai/chat", "/api/translate").permitAll()
                // 4. Role-based Protected areas
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/cart/**", "/checkout", "/place-order", "/my-orders/**", "/profile", "/update-profile", "/save-review", "/download-invoice/**", "/api/order/status/**").hasRole("USER")
                // 5. Fail-Closed: Chặn toàn bộ route còn lại, bắt buộc phải authenticated
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/do-login")
                .successHandler(mySuccessHandler())
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(-1)
                .sessionRegistry(sessionRegistry())
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/logout", "POST"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler mySuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            HttpSession session = request.getSession();
            String email = authentication.getName();

            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                Admin admin = adminRepo.findByEmail(email);
                Admin safeAdmin = Admin.builder()
                        .id(admin.getId())
                        .email(admin.getEmail())
                        .name(admin.getName())
                        .addedDate(admin.getAddedDate())
                        .password(null) // PREVENT LEAK
                        .build();
                session.setAttribute("admin", safeAdmin);
                response.sendRedirect("/admin/dashboard");
            } else {
                Customer customer = customerRepo.findByEmail(email);
                Customer safeCustomer = Customer.builder()
                        .id(customer.getId())
                        .email(customer.getEmail())
                        .name(customer.getName())
                        .phone(customer.getPhone())
                        .address(customer.getAddress())
                        .gender(customer.getGender())
                        .pinCode(customer.getPinCode())
                        .addedDate(customer.getAddedDate())
                        .password(null) // PREVENT LEAK
                        .build();
                session.setAttribute("user", safeCustomer);
                response.sendRedirect("/");
            }
        };
    }
}