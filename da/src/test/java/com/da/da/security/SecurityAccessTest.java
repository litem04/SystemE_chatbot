package com.da.da.security;

import com.da.da.config.SecurityConfig;
import com.da.da.controller.AuthController;
import com.da.da.controller.PaymentWebhookController;
import com.da.da.repository.AdminRepository;
import com.da.da.repository.CustomerRepository;
import com.da.da.service.AuthService;
import com.da.da.service.CustomUserDetailsService;
import com.da.da.service.PaymentWebhookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, PaymentWebhookController.class})
@Import(SecurityConfig.class)
@org.springframework.test.context.TestPropertySource(properties = "payment.webhook.secret=dummy")
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private AuthService authService;

    @MockBean
    private PaymentWebhookService paymentWebhookService;

    @Test
    @DisplayName("Khách vãng lai (Anonymous) truy cập /admin/dashboard bị chuyển hướng sang /login")
    void testAdminRoute_Anonymous_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = {"USER"})
    @DisplayName("User với vai trò ROLE_USER truy cập /admin/dashboard bị 403 Forbidden")
    void testAdminRoute_CustomerRole_Forbidden() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = {"USER"})
    @DisplayName("Kiểm tra CSRF: POST không có CSRF token vào endpoint người dùng bị từ chối 403 Forbidden")
    void testCsrfProtection_MissingToken_Forbidden() throws Exception {
        mockMvc.perform(post("/update-profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = {"USER"})
    @DisplayName("Kiểm tra CSRF: POST có CSRF token hợp lệ được phép đi qua filter vào controller")
    void testCsrfProtection_ValidToken_Success() throws Exception {
        when(customUserDetailsService.findByEmail(any())).thenReturn(new com.da.da.entity.Customer());

        mockMvc.perform(post("/update-profile")
                        .with(csrf())
                        .flashAttr("customer", new com.da.da.entity.Customer()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Kiểm tra ngoại lệ CSRF: Webhook /api/** được phép gửi POST không cần CSRF token")
    void testWebhookEndpoint_CsrfIgnored() throws Exception {
        when(paymentWebhookService.processVietQrWebhook(any(), any(), any()))
                .thenReturn(new PaymentWebhookService.WebhookProcessResult(false, 401, "Invalid webhook secret", null));

        com.da.da.dto.PaymentWebhookRequest payload = com.da.da.dto.PaymentWebhookRequest.builder()
                .description("THANHTOAN DH101")
                .amount(java.math.BigDecimal.valueOf(500000))
                .transactionId("FT123")
                .reference("REF123")
                .build();

        mockMvc.perform(post("/api/webhook/vietqr")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload)))
                .andExpect(status().isUnauthorized());
    }
}
