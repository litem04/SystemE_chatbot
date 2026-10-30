package com.techgear.admin.controller;

import com.techgear.admin.dto.DailyRevenueProjection;
import com.techgear.identity.entity.Customer;
import com.techgear.order.entity.Order;
import com.techgear.order.entity.OrderDetail;
import com.techgear.catalog.entity.Product;
import com.techgear.order.entity.enums.OrderStatus;
import com.techgear.admin.service.AdminCustomerService;
import com.techgear.order.service.OrderService;
import com.techgear.catalog.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/api")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDataController {

    private final ProductService productService;
    private final AdminCustomerService customerService;
    private final OrderService orderService;

    public AdminDataController(ProductService productService,
                               AdminCustomerService customerService,
                               OrderService orderService) {
        this.productService = productService;
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        List<RevenuePoint> revenue = orderService.getDailyRevenueStatistics().stream()
                .map(point -> new RevenuePoint(point.getOrderDay(), safeAmount(point.getTotalAmount())))
                .toList();

        return new DashboardResponse(
                productService.countProducts(),
                customerService.countCustomers(),
                orderService.countOrders(),
                safeAmount(orderService.calculateTotalRevenue()),
                safeAmount(orderService.calculateShippingRevenue()),
                revenue
        );
    }

    @GetMapping("/products")
    public PageResponse<ProductResponse> products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Product> result = productService.searchAdminProducts(keyword, category, page, size);
        return PageResponse.from(result, ProductResponse::from);
    }

    @GetMapping("/orders")
    public PageResponse<OrderResponse> orders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Order> result = orderService.getOrdersPaged(page, size, status);
        return PageResponse.from(result, OrderResponse::from);
    }

    @GetMapping("/orders/{id}")
    public OrderDetailResponse order(@PathVariable Integer id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng " + id);
        }

        List<OrderLineResponse> lines = orderService.getOrderDetails(order).stream()
                .map(OrderLineResponse::from)
                .toList();
        return OrderDetailResponse.from(order, lines);
    }

    @GetMapping("/customers")
    public PageResponse<CustomerResponse> customers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Customer> result = customerService.searchCustomers(keyword, page, size);
        return PageResponse.from(result, CustomerResponse::from);
    }

    private static BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    public record DashboardResponse(long productCount,
                                    long customerCount,
                                    long orderCount,
                                    BigDecimal deliveredRevenue,
                                    BigDecimal shippingRevenue,
                                    List<RevenuePoint> dailyRevenue) { }

    public record RevenuePoint(String day, BigDecimal amount) { }

    public record PageResponse<T>(List<T> content,
                                  int page,
                                  int size,
                                  long totalElements,
                                  int totalPages,
                                  boolean first,
                                  boolean last) {
        private static <S, T> PageResponse<T> from(Page<S> page, java.util.function.Function<S, T> mapper) {
            return new PageResponse<>(page.getContent().stream().map(mapper).toList(), page.getNumber(),
                    page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
        }
    }

    public record ProductResponse(Long id,
                                  String name,
                                  String category,
                                  BigDecimal price,
                                  BigDecimal mrpPrice,
                                  BigDecimal discountPrice,
                                  Integer discountLimit,
                                  Integer discountSold,
                                  Integer stock,
                                  String image,
                                  String active) {
        private static ProductResponse from(Product product) {
            return new ProductResponse(product.getId(), product.getName(), product.getProductCategory(),
                    product.getPrice(), product.getMrpPrice(), product.getDiscountPrice(), product.getDiscountLimit(),
                    product.getDiscountSold(), product.getStock(), product.getImage(), product.getActive());
        }
    }

    public record OrderResponse(Integer id,
                                String customerName,
                                String email,
                                String phone,
                                BigDecimal total,
                                String paymentMode,
                                String paymentStatus,
                                String orderStatus,
                                java.util.Date orderDate) {
        private static OrderResponse from(Order order) {
            return new OrderResponse(order.getId(), order.getCustomerName(), order.getEmailId(),
                    order.getMobileNumber(), order.getTotalAmount(), order.getPaymentModeName(),
                    order.getPaymentStatusName(), order.getOrderStatusName(), order.getOrderDate());
        }
    }

    public record OrderLineResponse(String productName,
                                    Integer quantity,
                                    BigDecimal unitPrice,
                                    BigDecimal totalPrice) {
        private static OrderLineResponse from(OrderDetail detail) {
            return new OrderLineResponse(detail.getProductName(), detail.getQuantity(), detail.getPrice(),
                    detail.getTotalPrice());
        }
    }

    public record OrderDetailResponse(OrderResponse order, List<OrderLineResponse> lines) {
        private static OrderDetailResponse from(Order order, List<OrderLineResponse> lines) {
            return new OrderDetailResponse(OrderResponse.from(order), lines);
        }
    }

    public record CustomerResponse(Integer id, String name, String email, String phone, String address) {
        private static CustomerResponse from(Customer customer) {
            return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(),
                    customer.getPhone(), customer.getAddress());
        }
    }
}