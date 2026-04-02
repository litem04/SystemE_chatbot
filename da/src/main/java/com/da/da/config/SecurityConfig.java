package com.da.da.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import com.da.da.entity.Admin;
import com.da.da.entity.Customer;
import com.da.da.repository.AdminRepository;
import com.da.da.repository.CustomerRepository;
import com.da.da.service.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private CustomUserDetailsService customUserDetailsService; 
    @Autowired private AdminRepository adminRepo;
    @Autowired private CustomerRepository customerRepo;

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); 
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
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // 1. CHỈ CẦN TRUYỀN CHUỖI STRING, BỎ HẾT AntPathRequestMatcher ĐI
            		.requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")
                    .requestMatchers(antMatcher("/checkout")).hasRole("USER") 
                    .requestMatchers(antMatcher("/cart/**")).hasRole("USER") 
                    .requestMatchers(antMatcher("/my-orders/**")).hasRole("USER")
                .anyRequest().permitAll()
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
                // 2. SỬA CẢ CHỖ LOGOUT CHO CHUẨN SPRING BOOT 3
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler mySuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                HttpSession session = request.getSession();
                String email = authentication.getName(); 
                
                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    Admin admin = adminRepo.findByEmail(email);
                    session.setAttribute("admin", admin); 
                    response.sendRedirect("/admin/dashboard");
                } else {
                    Customer customer = customerRepo.findByEmail(email);
                    session.setAttribute("user", customer); 
                    response.sendRedirect("/");
                }
            }
        };
    }
}