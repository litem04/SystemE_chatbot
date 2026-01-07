//package com.da.da.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // IMPORT QUAN TRỌNG
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    // KHÔNG TẠO bean mvcHandlerMappingIntrospector NỮA (Để tránh lỗi trùng)
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(auth -> auth
//                // Dùng AntPathRequestMatcher: Cách an toàn nhất, không lo lỗi Bean
//                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/admin/login")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/uploads/**")).permitAll()
//                
//                // Cho phép tất cả các request khác
//                .anyRequest().permitAll()
//            )
//            .formLogin(AbstractHttpConfigurer::disable)
//            .logout(logout -> logout.permitAll());
//
//        return http.build();
//    }
//}
package com.da.da.config;

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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private CustomUserDetailsService customUserDetailsService; // Autowired class mình vừa tạo
    @Autowired private AdminRepository adminRepo;
    @Autowired private CustomerRepository customerRepo;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Tạm thời chưa mã hóa
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(customUserDetailsService); // Set cái service mình vừa viết vào đây
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/checkout")).hasRole("USER") 
                .requestMatchers(new AntPathRequestMatcher("/cart/**")).hasRole("USER") 
                .requestMatchers(new AntPathRequestMatcher("/my-orders/**")).hasRole("USER")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login") // Trang login chung
                .loginProcessingUrl("/do-login") // Link xử lý (Không cần viết Controller cho cái này)
                .successHandler(mySuccessHandler()) // <--- Xử lý sau khi đăng nhập thành công
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }

    // --- HÀM QUAN TRỌNG: Xử lý Session và Chuyển hướng ---
    @Bean
    public AuthenticationSuccessHandler mySuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                HttpSession session = request.getSession();
                String email = authentication.getName(); // Lấy email người vừa đăng nhập
                
                // Kiểm tra xem người này là Admin hay User
                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    // 1. Lấy thông tin Admin từ DB
                    Admin admin = adminRepo.findByEmail(email);
                    // 2. Nhét vào Session (để code cũ hoạt động)
                    session.setAttribute("admin", admin); 
                    // 3. Chuyển hướng vào Dashboard
                    response.sendRedirect("/admin/dashboard");
                    
                } else {
                    // 1. Lấy thông tin Khách từ DB
                    Customer customer = customerRepo.findByEmail(email);
                    // 2. Nhét vào Session "user" (Code Cart/Order của bạn đang dùng cái này)
                    session.setAttribute("user", customer); 
                    // 3. Chuyển hướng về Trang chủ
                    response.sendRedirect("/");
                }
            }
        };
    }
}