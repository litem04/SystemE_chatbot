package com.da.da.integration;

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
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.service.CartService;
import com.da.da.service.OrderService;
import com.da.da.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityAndIdorIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Customer userA;
    private Customer userB;
    private Product testProduct;
    private Cart userBCartItem;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        userA = customerRepository.save(Customer.builder()
                .name("Attacker A")
                .email("attacker@example.com")
                .password("secret")
                .build());

        userB = customerRepository.save(Customer.builder()
                .name("Victim B")
                .email("victim@example.com")
                .password("secret")
                .build());

        testProduct = productRepository.save(Product.builder()
                .name("Mechanical Keyboard")
                .price(BigDecimal.valueOf(1500000))
                .stock(50)
                .build());

        userBCartItem = cartRepository.save(Cart.builder()
                .customerId(Long.valueOf(userB.getId()))
                .productId(testProduct.getId())
                .product(testProduct)
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(3000000))
                .build());
    }

    @AfterEach
    void tearDown() {
        cartRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Chống IDOR: User A không thể xóa giỏ hàng của User B bằng Cart ID")
    void testCartItemRemoval_IdorProtection() {
        // User A cố tình gửi ID món hàng của User B để xóa
        boolean removed = cartService.removeItemById(Long.valueOf(userA.getId()), userBCartItem.getId());

        // Phải trả về false và bản ghi vẫn còn nguyên vẹn trong DB
        assertFalse(removed, "Hệ thống phải từ chối hành vi xóa chéo tài khoản");
        assertTrue(cartRepository.existsById(userBCartItem.getId()), "Giỏ hàng của User B không được phép bị xóa");
    }

    @Test
    @DisplayName("Chống IDOR: User A không thể sửa số lượng giỏ hàng của User B")
    void testCartItemUpdate_IdorProtection() {
        // User A cố tình sửa số lượng giỏ hàng của User B thành 10
        cartService.updateCartItemQuantity(Long.valueOf(userA.getId()), userBCartItem.getId(), 10);

        Cart reloaded = cartRepository.findById(userBCartItem.getId()).orElseThrow();
        assertEquals(2, reloaded.getQuantity(), "Số lượng giỏ hàng của User B không được thay đổi bởi User A");
    }

    @Test
    @DisplayName("Validation sản phẩm: Giá khuyến mãi không được lớn hơn giá gốc")
    void testProductValidation_DiscountPriceGreaterThanPrice_ThrowsException() {
        com.da.da.dto.ProductRequestDTO invalidProduct = new com.da.da.dto.ProductRequestDTO();
        invalidProduct.setName("Invalid Product");
        invalidProduct.setPrice(BigDecimal.valueOf(100000));
        invalidProduct.setDiscountPrice(BigDecimal.valueOf(150000)); // Khuyến mãi > Gốc (Vô lý)
        invalidProduct.setStock(10);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productService.addProduct(invalidProduct, null));

        assertTrue(ex.getMessage().contains("không được lớn hơn giá gốc"));
    }

    @Test
    @DisplayName("Validation sản phẩm: Giá gốc không được là số âm")
    void testProductValidation_NegativePrice_ThrowsException() {
        com.da.da.dto.ProductRequestDTO invalidProduct = new com.da.da.dto.ProductRequestDTO();
        invalidProduct.setName("Negative Price Product");
        invalidProduct.setPrice(BigDecimal.valueOf(-50000));
        invalidProduct.setStock(10);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productService.addProduct(invalidProduct, null));

        assertTrue(ex.getMessage().contains("không được âm"));
    }

    @Test
    @DisplayName("Trạng thái đơn hàng: Không thể thay đổi trạng thái khi đơn đã DELIVERED hoặc CANCELLED")
    void testOrderStatus_TerminalStatusCannotBeChanged() {
        Order deliveredOrder = orderRepository.save(Order.builder()
                .customerName("Customer")
                .emailId("customer@example.com")
                .orderDate(new Date())
                .paymentMode(PaymentMode.COD)
                .paymentStatus(PaymentStatus.PAID)
                .orderStatus(OrderStatus.DELIVERED)
                .productTotalPrice(BigDecimal.valueOf(1000000))
                .build());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                orderService.updateOrderStatus(deliveredOrder.getId(), OrderStatus.PROCESSING));

        assertTrue(ex.getMessage().contains("Đơn hàng đã kết thúc"));
    }

    @Test
    @DisplayName("Validation giỏ hàng: Số lượng thêm vào giỏ được tự động giới hạn tối đa 99")
    void testCartQuantity_UpperLimitEnforced() {
        cartService.addToCart(userA.getEmail(), testProduct.getId(), 500); // Đặt 500 chiếc

        Cart cart = cartRepository.findByCustomerIdAndProductId(Long.valueOf(userA.getId()), testProduct.getId());
        assertNotNull(cart);
        assertTrue(cart.getQuantity() <= 99, "Số lượng trong giỏ hàng phải được giới hạn tối đa 99");
    }
}
