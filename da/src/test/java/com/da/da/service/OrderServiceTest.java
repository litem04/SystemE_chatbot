package com.da.da.service;

import com.da.da.dto.PlaceOrderRequest;
import com.da.da.entity.Cart;
import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.entity.Product;
import com.da.da.entity.enums.OrderStatus;
import com.da.da.entity.enums.PaymentMode;
import com.da.da.entity.enums.PaymentStatus;
import com.da.da.repository.CartRepository;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderService orderService;

    private Customer testCustomer;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1)
                .email("test@example.com")
                .name("Nguyen Van A")
                .phone("0901234567")
                .build();

        testProduct = Product.builder()
                .id(100L)
                .name("Laptop Gaming")
                .price(BigDecimal.valueOf(20000000))
                .discountPrice(BigDecimal.valueOf(18000000))
                .discountLimit(5)
                .discountSold(0)
                .stock(10)
                .build();

        testCart = Cart.builder()
                .id(10L)
                .customerId(1L)
                .productId(100L)
                .product(testProduct)
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(18000000))
                .build();
    }

    @Test
    @DisplayName("Đặt hàng thành công với giá khuyến mãi và trừ tồn kho chính xác")
    void testPlaceOrder_Success() {
        when(cartRepository.findByCustomerId(1L)).thenReturn(List.of(testCart));
        when(productRepository.findByIdWithLock(100L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(42);
            return o;
        });

        Order createdOrder = orderService.placeOrder(testCustomer, "Hà Nội", "0901234567", "COD");

        assertNotNull(createdOrder);
        assertEquals(42, createdOrder.getId());
        assertEquals(OrderStatus.PENDING, createdOrder.getOrderStatus());
        assertEquals(PaymentStatus.UNPAID, createdOrder.getPaymentStatus());
        assertEquals(PaymentMode.COD, createdOrder.getPaymentMode());
        assertEquals(BigDecimal.valueOf(18000000), createdOrder.getProductTotalPrice());

        // Kiểm tra trừ tồn kho: 10 - 1 = 9
        assertEquals(9, testProduct.getStock());
        // Kiểm tra tăng suất đã bán: 0 + 1 = 1
        assertEquals(1, testProduct.getDiscountSold());

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderDetailRepository, times(1)).save(any());
        verify(cartRepository, times(1)).deleteAll(any());
    }

    @Test
    @DisplayName("Đặt hàng với phương thức VIETQR thì orderStatus khởi tạo là WAITING_FOR_PAYMENT")
    void testPlaceOrder_VietQR_WaitingForPayment() {
        when(cartRepository.findByCustomerId(1L)).thenReturn(List.of(testCart));
        when(productRepository.findByIdWithLock(100L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlaceOrderRequest request = PlaceOrderRequest.builder()
                .address("TP Hồ Chí Minh")
                .phone("0909999999")
                .paymentMode("VIETQR")
                .build();

        Order order = orderService.placeOrder(testCustomer, request);

        assertEquals(OrderStatus.WAITING_FOR_PAYMENT, order.getOrderStatus());
        assertEquals(PaymentMode.VIETQR, order.getPaymentMode());
    }

    @Test
    @DisplayName("Đặt hàng thất bại khi giỏ hàng trống")
    void testPlaceOrder_EmptyCart_ThrowsException() {
        when(cartRepository.findByCustomerId(1L)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () ->
                orderService.placeOrder(testCustomer, "Hà Nội", "0901234567", "COD"));
    }

    @Test
    @DisplayName("Đặt hàng thất bại khi sản phẩm hết tồn kho")
    void testPlaceOrder_OutOfStock_ThrowsException() {
        testProduct.setStock(0);
        when(cartRepository.findByCustomerId(1L)).thenReturn(List.of(testCart));
        when(productRepository.findByIdWithLock(100L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(IllegalStateException.class, () ->
                orderService.placeOrder(testCustomer, "Hà Nội", "0901234567", "COD"));
    }

    @Test
    @DisplayName("Hủy đơn hàng tự động hoàn tồn kho cho các sản phẩm")
    void testUpdateOrderStatus_Cancelled_RestoresStock() {
        Order existingOrder = Order.builder()
                .id(10)
                .orderStatus(OrderStatus.PROCESSING)
                .emailId("test@example.com")
                .build();

        OrderDetail detail = OrderDetail.builder()
                .order(existingOrder)
                .product(testProduct)
                .quantity(2)
                .build();

        when(orderRepository.findByIdWithLock(10)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findByIdWithLock(100L)).thenReturn(Optional.of(testProduct));
        when(orderDetailRepository.findByOrder(existingOrder)).thenReturn(List.of(detail));

        orderService.updateOrderStatus(10, OrderStatus.CANCELLED);

        // Tồn ban đầu 10, hoàn lại 2 -> 12
        assertEquals(12, testProduct.getStock());
        assertEquals(OrderStatus.CANCELLED, existingOrder.getOrderStatus());
        verify(productRepository, times(1)).save(testProduct);
        verify(orderRepository, times(1)).save(existingOrder);
    }

    @Test
    @DisplayName("Admin xác nhận thanh toán thủ công chuyển paymentStatus sang PAID")
    void testConfirmPaymentManually() {
        Order order = Order.builder()
                .id(50)
                .orderStatus(OrderStatus.WAITING_FOR_PAYMENT)
                .paymentStatus(PaymentStatus.UNPAID)
                .emailId("test@example.com")
                .build();

        when(orderRepository.findById(50)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.confirmPaymentManually(50);

        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
