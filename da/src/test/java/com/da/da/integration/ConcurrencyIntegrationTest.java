package com.da.da.integration;

import com.da.da.dto.PaymentWebhookRequest;
import com.da.da.dto.PlaceOrderRequest;
import com.da.da.entity.Cart;
import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.Product;
import com.da.da.entity.enums.OrderStatus;
import com.da.da.entity.enums.PaymentMode;
import com.da.da.entity.enums.PaymentStatus;
import com.da.da.repository.CartRepository;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.service.OrderService;
import com.da.da.service.PaymentWebhookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConcurrencyIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentWebhookService paymentWebhookService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    private Customer user1;
    private Customer user2;
    private Product scarceProduct;

    @BeforeEach
    void setUp() {
        // Chuẩn bị dữ liệu sạch cho test
        cartRepository.deleteAll();
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        user1 = customerRepository.save(Customer.builder()
                .name("User One")
                .email("user1@example.com")
                .phone("0901111111")
                .password("encoded_pass")
                .build());

        user2 = customerRepository.save(Customer.builder()
                .name("User Two")
                .email("user2@example.com")
                .phone("0902222222")
                .password("encoded_pass")
                .build());

        // Sản phẩm chỉ còn duy nhất 1 chiếc trong kho (stock = 1)
        scarceProduct = productRepository.save(Product.builder()
                .name("IPhone 15 Pro Max")
                .price(BigDecimal.valueOf(30000000))
                .stock(1)
                .discountSold(0)
                .build());
    }

    @AfterEach
    void tearDown() {
        cartRepository.deleteAll();
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Race Condition Test: 2 khách hàng đồng thời mua 1 sản phẩm cuối cùng (stock = 1) -> Chỉ 1 người thành công, không bị overselling")
    void testConcurrentCheckout_NoOverselling() throws Exception {
        // Cả 2 user đều đã thêm món hàng này vào giỏ hàng
        cartRepository.save(Cart.builder()
                .customerId(Long.valueOf(user1.getId()))
                .productId(scarceProduct.getId())
                .product(scarceProduct)
                .quantity(1)
                .totalPrice(scarceProduct.getPrice())
                .build());

        cartRepository.save(Cart.builder()
                .customerId(Long.valueOf(user2.getId()))
                .productId(scarceProduct.getId())
                .product(scarceProduct)
                .quantity(1)
                .totalPrice(scarceProduct.getPrice())
                .build());

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Callable<Void> checkoutUser1 = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                PlaceOrderRequest req = PlaceOrderRequest.builder()
                        .address("123 Phố Huế, Hà Nội")
                        .phone("0901111111")
                        .paymentMode("COD")
                        .build();
                orderService.placeOrder(user1, req);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
            return null;
        };

        Callable<Void> checkoutUser2 = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                PlaceOrderRequest req = PlaceOrderRequest.builder()
                        .address("456 Nguyễn Huệ, TP.HCM")
                        .phone("0902222222")
                        .paymentMode("COD")
                        .build();
                orderService.placeOrder(user2, req);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
            return null;
        };

        Future<Void> future1 = executor.submit(checkoutUser1);
        Future<Void> future2 = executor.submit(checkoutUser2);

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown(); // Bắn 2 request chạy song song cùng 1 tích tắc

        future1.get(10, TimeUnit.SECONDS);
        future2.get(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Kiểm tra kết quả: Chính xác 1 đơn thành công, 1 đơn thất bại do hết tồn kho
        assertEquals(1, successCount.get(), "Chỉ được phép đúng 1 đơn hàng thành công");
        assertEquals(1, failCount.get(), "Đơn còn lại phải bị từ chối do hết tồn kho");

        // Kiểm tra tồn kho trong Database: Phải là 0, TUYỆT ĐỐI không được âm (-1)
        Product reloadedProduct = productRepository.findById(scarceProduct.getId()).orElseThrow();
        assertEquals(0, reloadedProduct.getStock(), "Tồn kho phải bằng 0, không bị bán âm (oversold)");
    }

    @Test
    @DisplayName("Race Condition Test: 2 Webhook đồng thời gọi xác nhận cho 1 đơn hàng -> Pessimistic Lock đảm bảo xử lý duy nhất 1 lần (Idempotency)")
    void testConcurrentWebhook_IdempotentProcessing() throws Exception {
        // Tạo đơn hàng ở trạng thái WAITING_FOR_PAYMENT
        Order testOrder = orderRepository.save(Order.builder()
                .customerName("User One")
                .emailId("user1@example.com")
                .mobileNumber("0901111111")
                .address("Hà Nội")
                .orderDate(new Date())
                .paymentMode(PaymentMode.VIETQR)
                .paymentStatus(PaymentStatus.UNPAID)
                .orderStatus(OrderStatus.WAITING_FOR_PAYMENT)
                .productTotalPrice(BigDecimal.valueOf(500000))
                .build());

        String secret = "dev_webhook_secret_key_change_in_prod";
        PaymentWebhookRequest payload = PaymentWebhookRequest.builder()
                .description("THANHTOAN DH" + testOrder.getId())
                .amount(BigDecimal.valueOf(500000))
                .transactionId("TXN_CONCURRENT_123")
                .build();

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<PaymentWebhookService.WebhookProcessResult> task = () -> {
            readyLatch.countDown();
            startLatch.await();
            return paymentWebhookService.processVietQrWebhook(secret, secret, payload);
        };

        Future<PaymentWebhookService.WebhookProcessResult> f1 = executor.submit(task);
        Future<PaymentWebhookService.WebhookProcessResult> f2 = executor.submit(task);

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown(); // Bắn 2 webhook đồng thời

        PaymentWebhookService.WebhookProcessResult res1 = f1.get(10, TimeUnit.SECONDS);
        PaymentWebhookService.WebhookProcessResult res2 = f2.get(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Cả 2 đều trả về HTTP 200 OK cho đối tác ngân hàng
        assertEquals(200, res1.httpStatus());
        assertEquals(200, res2.httpStatus());

        // Một trong 2 thread thực hiện cập nhật Success, thread còn lại phát hiện Already processed
        boolean oneIsSuccess = "Success".equals(res1.message()) || "Success".equals(res2.message());
        boolean oneIsAlreadyProcessed = "Already processed".equals(res1.message()) || "Order already paid".equals(res1.message())
                || "Already processed".equals(res2.message()) || "Order already paid".equals(res2.message());

        assertTrue(oneIsSuccess, "Phải có đúng 1 thread thực hiện cập nhật thành công");
        assertTrue(oneIsAlreadyProcessed, "Thread đồng thời còn lại phải phát hiện đã thanh toán và trả về 200 OK an toàn");

        // Kiểm tra trạng thái cuối cùng của đơn hàng trong DB
        Order reloadedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(PaymentStatus.PAID, reloadedOrder.getPaymentStatus());
        assertEquals(OrderStatus.PROCESSING, reloadedOrder.getOrderStatus());
        assertEquals("TXN_CONCURRENT_123", reloadedOrder.getTransactionId());
    }
}
