package com.da.da.security;

import com.da.da.config.SecurityConfig;
import com.da.da.controller.AdminAiController;
import com.da.da.controller.AuthController;
import com.da.da.controller.PaymentWebhookController;
import com.da.da.repository.AdminRepository;
import com.da.da.repository.CustomerRepository;
import com.da.da.service.AuthService;
import com.da.da.service.AdminAiService;
import com.da.da.service.CustomUserDetailsService;
import com.da.da.service.IngestionService;
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

@WebMvcTest(controllers = {AuthController.class, PaymentWebhookController.class, AdminAiController.class})
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

        @MockBean
        private AdminAiService adminAiService;

        @MockBean
        private IngestionService ingestionService;

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
                        .flashAttr("customer", new com.da.da.dto.UpdateProfileRequest()))
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

        @Test
        @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
        @DisplayName("Admin AI API trả JSON khi có quyền ADMIN và CSRF token")
        void testAdminAiApi_WithCsrf_ReturnsJsonData() throws Exception {
                when(adminAiService.processAdminChat("Báo cáo doanh thu")).thenReturn("Tổng doanh thu: 100000");

                mockMvc.perform(post("/admin/api/ai/chat")
                                                .with(csrf())
                                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                                .content("{\"message\":\"Báo cáo doanh thu\"}"))
                                .andExpect(status().isOk())
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.reply")
                                                .value("Tổng doanh thu: 100000"));
        }

        @Test
        @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
        @DisplayName("Admin AI API từ chối POST thiếu CSRF token")
        void testAdminAiApi_WithoutCsrf_ReturnsForbidden() throws Exception {
                mockMvc.perform(post("/admin/api/ai/chat")
                                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                                .content("{\"message\":\"test\"}"))
                                .andExpect(status().isForbidden());
        }

    @Test
    @DisplayName("Đăng ký với số điện thoại sai định dạng: trả về view client/customer-register với mã lỗi phone, không redirect và giữ nguyên dữ liệu")
    void testRegister_InvalidPhone_ReturnsRegisterViewWithFieldError() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("name", "Nguyễn Văn A")
                        .param("email", "vana@example.com")
                        .param("password", "123456")
                        .param("phone", "12345")
                        .param("address", "Hà Nội")
                        .param("pinCode", "100000")
                        .param("gender", "Nam"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view().name("client/customer-register"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeHasFieldErrors("customer", "phone"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attribute("customer", org.hamcrest.Matchers.hasProperty("name", org.hamcrest.Matchers.is("Nguyễn Văn A"))));
    }

    @Test
    @DisplayName("Đăng ký với email trùng lặp: AuthService ném ngoại lệ -> gắn lỗi vào trường email, trả về view không redirect")
    void testRegister_DuplicateEmail_AttachesErrorToEmailField() throws Exception {
        when(authService.register(any())).thenThrow(new IllegalArgumentException("Email này đã được đăng ký!"));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("name", "Nguyễn Văn B")
                        .param("email", "trung@example.com")
                        .param("password", "123456")
                        .param("phone", "0987654321")
                        .param("address", "TP HCM")
                        .param("pinCode", "700000")
                        .param("gender", "Nữ"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view().name("client/customer-register"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeHasFieldErrors("customer", "email"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attribute("customer", org.hamcrest.Matchers.hasProperty("email", org.hamcrest.Matchers.is("trung@example.com"))));
    }

    @Test
    @DisplayName("Đăng ký thành công: chuyển hướng sang /login với flash attribute")
    void testRegister_Success_RedirectsToLogin() throws Exception {
        when(authService.register(any())).thenReturn(new com.da.da.entity.Customer());

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("name", "Nguyễn Văn C")
                        .param("email", "vanc@example.com")
                        .param("password", "123456")
                        .param("phone", "0987654321")
                        .param("address", "Đà Nẵng")
                        .param("pinCode", "550000")
                        .param("gender", "Nam"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attributeExists("successMessage"));
    }
}
