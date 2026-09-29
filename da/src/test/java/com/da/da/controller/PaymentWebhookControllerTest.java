package com.da.da.controller;

import com.da.da.dto.PaymentWebhookRequest;
import com.da.da.entity.Order;
import com.da.da.entity.enums.OrderStatus;
import com.da.da.entity.enums.PaymentStatus;
import com.da.da.repository.OrderRepository;
import com.da.da.service.PaymentWebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentWebhookControllerTest {

    private static final String VALID_SECRET = "test_super_secret_webhook_key";

    private MockMvc mockMvc;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private PaymentWebhookService paymentWebhookService;

    private PaymentWebhookController paymentWebhookController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Order testOrder;

    @BeforeEach
    void setUp() {
        paymentWebhookService = new PaymentWebhookService(orderRepository, messagingTemplate);
        paymentWebhookController = new PaymentWebhookController(paymentWebhookService);
        ReflectionTestUtils.setField(paymentWebhookController, "configuredSecret", VALID_SECRET);

        mockMvc = MockMvcBuilders.standaloneSetup(paymentWebhookController).build();

        testOrder = Order.builder()
                .id(101)
                .orderStatus(OrderStatus.WAITING_FOR_PAYMENT)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentMode(com.da.da.entity.enums.PaymentMode.VIETQR)
                .productTotalPrice(BigDecimal.valueOf(500000))
                .build();
    }

    @Test
    @DisplayName("Webhook bị từ chối 401 khi thiếu header X-Webhook-Secret")
    void testWebhook_MissingSecretHeader_Returns401() throws Exception {
        PaymentWebhookRequest request = PaymentWebhookRequest.builder()
                .description("THANHTOAN DH101")
                .amount(BigDecimal.valueOf(500000))
                .transactionId("FT2401019999")
                .reference("REF123")
                .build();

        mockMvc.perform(post("/api/webhook/vietqr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"));

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Webhook bị từ chối 401 khi sai secret header (Constant-Time comparison)")
    void testWebhook_WrongSecretHeader_Returns401() throws Exception {
        PaymentWebhookRequest request = PaymentWebhookRequest.builder()
                .description("THANHTOAN DH101")
                .amount(BigDecimal.valueOf(500000))
                .transactionId("FT2401019999")
                .reference("REF123")
                .build();

        mockMvc.perform(post("/api/webhook/vietqr")
                        .header("X-Webhook-Secret", "wrong_secret_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"));

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Webhook bị từ chối 400 khi số tiền không khớp với đơn hàng (Financial Accuracy)")
    void testWebhook_AmountMismatch_Returns400() throws Exception {
        when(orderRepository.findByIdWithLock(101)).thenReturn(Optional.of(testOrder));

        // Đơn giá 500.000 nhưng chuyển 400.000
        PaymentWebhookRequest request = PaymentWebhookRequest.builder()
                .description("THANHTOAN DH101")
                .amount(BigDecimal.valueOf(400000))
                .transactionId("FT2401019999")
                .reference("REF123")
                .build();

        mockMvc.perform(post("/api/webhook/vietqr")
                        .header("X-Webhook-Secret", VALID_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Webhook thành công 200: Cập nhật PAID, PROCESSING, lưu transactionId và gửi realtime websocket")
    void testWebhook_ValidPayment_Success() throws Exception {
        when(orderRepository.findByIdWithLock(101)).thenReturn(Optional.of(testOrder));

        PaymentWebhookRequest request = PaymentWebhookRequest.builder()
                .description("THANHTOAN DH101")
                .amount(BigDecimal.valueOf(500000))
                .transactionId("FT2401019999")
                .reference("REF123")
                .build();

        mockMvc.perform(post("/api/webhook/vietqr")
                        .header("X-Webhook-Secret", VALID_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.orderId").value(101));

        assert(testOrder.isPaid());
        assert(testOrder.getOrderStatus() == OrderStatus.PROCESSING);
        assert("FT2401019999".equals(testOrder.getTransactionId()));

        verify(orderRepository, times(1)).save(testOrder);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/admin/notifications"), anyString());
    }

    @Test
    @DisplayName("Chống xử lý trùng (Idempotency): Trả về 200 OK ngay nếu đơn hàng đã được thanh toán trước đó")
    void testWebhook_DuplicateTransaction_IdempotentSkip() throws Exception {
        testOrder.setPaymentStatus(PaymentStatus.PAID);
        testOrder.setOrderStatus(OrderStatus.PROCESSING);
        testOrder.setTransactionId("FT2401019999");

        when(orderRepository.findByIdWithLock(101)).thenReturn(Optional.of(testOrder));

        PaymentWebhookRequest request = PaymentWebhookRequest.builder()
                .description("THANHTOAN DH101")
                .amount(BigDecimal.valueOf(500000))
                .transactionId("FT2401019999")
                .reference("REF123")
                .build();

        mockMvc.perform(post("/api/webhook/vietqr")
                        .header("X-Webhook-Secret", VALID_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // Không thực hiện update hoặc ghi đè lại
        verify(orderRepository, never()).save(any());
    }
}
