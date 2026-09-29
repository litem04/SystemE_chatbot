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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository,
                        OrderDetailRepository orderDetailRepository,
                        ProductRepository productRepository,
                        CartRepository cartRepository,
                        EmailService emailService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.emailService = emailService;
    }

    /**
     * Xử lý đặt hàng qua PlaceOrderRequest DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public Order placeOrder(Customer user, PlaceOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Thông tin đơn hàng không hợp lệ.");
        }
        return placeOrder(user, request.getAddress(), request.getPhone(), request.getPaymentMode());
    }

    /**
     * Xử lý đặt hàng toàn diện theo chuẩn ACID (Atomic, Consistent, Isolated, Durable)
     */
    @Transactional(rollbackFor = Exception.class)
    public Order placeOrder(Customer user, String address, String phone, String paymentModeStr) {
        if (user == null) {
            throw new IllegalArgumentException("Người dùng chưa đăng nhập.");
        }

        List<Cart> cartItems = cartRepository.findByCustomerId(Long.valueOf(user.getId()));
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng của bạn đang trống.");
        }

        PaymentMode paymentMode = PaymentMode.fromString(paymentModeStr);
        OrderStatus initialStatus = (paymentMode == PaymentMode.VIETQR) ? OrderStatus.WAITING_FOR_PAYMENT : OrderStatus.PENDING;

        // 1. Khởi tạo đơn hàng
        Order order = Order.builder()
                .customerName(user.getName())
                .emailId(user.getEmail())
                .mobileNumber(phone)
                .address(address)
                .orderDate(new Date())
                .paymentMode(paymentMode)
                .paymentStatus(PaymentStatus.UNPAID)
                .orderStatus(initialStatus)
                .productTotalPrice(BigDecimal.ZERO)
                .build();

        Order savedOrder = orderRepository.save(order);
        BigDecimal grandTotal = BigDecimal.ZERO;

        // 2. Duyệt qua từng sản phẩm trong giỏ để trừ kho và tạo OrderDetail
        for (Cart item : cartItems) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                cartRepository.delete(item);
                continue;
            }

            // Khóa bi quan (Pessimistic Write Lock) ngăn chặn Race Condition / Overselling
            Product product = productRepository.findByIdWithLock(item.getProduct().getId())
                    .orElseThrow(() -> new IllegalStateException("Sản phẩm không còn tồn tại trong hệ thống."));

            if (entityManager != null) {
                entityManager.refresh(product);
            }

            int quantityBuy = item.getQuantity() != null ? item.getQuantity() : 1;
            int stock = product.getStock() != null ? product.getStock() : 0;

            if (stock < quantityBuy) {
                throw new IllegalStateException("Sản phẩm '" + product.getName() + "' không đủ tồn kho (còn: " + stock + ")");
            }

            // Tính toán giá chiết khấu hoặc giá gốc
            BigDecimal originalPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
            BigDecimal salePrice = (product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                    ? product.getDiscountPrice()
                    : originalPrice;

            int limit = product.getDiscountLimit() != null ? product.getDiscountLimit() : 0;
            int currentSold = product.getDiscountSold() != null ? product.getDiscountSold() : 0;
            int availableSlots = Math.max(0, limit - currentSold);

            int soldCountToAdd = 0;
            BigDecimal lineTotal;

            if (product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0 && availableSlots > 0) {
                if (quantityBuy <= availableSlots) {
                    lineTotal = salePrice.multiply(BigDecimal.valueOf(quantityBuy));
                    soldCountToAdd = quantityBuy;
                } else {
                    BigDecimal salePart = salePrice.multiply(BigDecimal.valueOf(availableSlots));
                    BigDecimal regularPart = originalPrice.multiply(BigDecimal.valueOf(quantityBuy - availableSlots));
                    lineTotal = salePart.add(regularPart);
                    soldCountToAdd = availableSlots;
                }
            } else {
                lineTotal = originalPrice.multiply(BigDecimal.valueOf(quantityBuy));
            }

            grandTotal = grandTotal.add(lineTotal);

            // Cập nhật tồn kho và số lượng đã bán flash sale
            product.setStock(stock - quantityBuy);
            if (soldCountToAdd > 0) {
                product.setDiscountSold(currentSold + soldCountToAdd);
            }
            productRepository.saveAndFlush(product);

            // Lưu OrderDetail
            BigDecimal averageUnitPrice = lineTotal.divide(BigDecimal.valueOf(quantityBuy), 2, RoundingMode.HALF_UP);
            OrderDetail detail = OrderDetail.builder()
                    .order(savedOrder)
                    .product(product)
                    .productName(product.getName())
                    .quantity(quantityBuy)
                    .price(averageUnitPrice)
                    .totalPrice(lineTotal)
                    .build();
            orderDetailRepository.save(detail);
        }

        // 3. Cập nhật lại tổng tiền chính xác cho đơn hàng
        savedOrder.setProductTotalPrice(grandTotal);
        savedOrder = orderRepository.save(savedOrder);

        // 4. Xóa sạch giỏ hàng của người dùng sau khi đặt hàng thành công
        cartRepository.deleteAll(cartItems);

        // 5. Gửi email xác nhận đơn hàng bất đồng bộ / an toàn
        try {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            emailService.sendOrderConfirmation(savedOrder.getEmailId(), String.valueOf(savedOrder.getId()), currencyFormat.format(grandTotal));
        } catch (Exception e) {
            log.warn("Không thể gửi email xác nhận cho đơn hàng #{}: {}", savedOrder.getId(), e.getMessage());
        }

        return savedOrder;
    }

    /**
     * Cập nhật trạng thái đơn hàng và tự động hoàn trả kho khi đơn bị hủy
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + orderId));

        OrderStatus oldStatus = order.getOrderStatus();
        if (oldStatus == OrderStatus.CANCELLED || oldStatus == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Đơn hàng đã kết thúc (" + oldStatus.name() + "), không thể thay đổi trạng thái!");
        }

        // Ràng buộc thanh toán nghiêm ngặt: Đơn hàng chuyển khoản (trả trước) nếu chưa thanh toán thì KHÔNG được giao hàng
        if (order.getPaymentMode() != PaymentMode.COD && order.getPaymentStatus() != PaymentStatus.PAID) {
            if (newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.DELIVERED) {
                throw new IllegalStateException("Đơn hàng thanh toán online (" + order.getPaymentMode()
                        + ") chưa hoàn tất thanh toán (UNPAID)! Không thể chuyển sang trạng thái " + newStatus.name()
                        + ". Vui lòng xác nhận thanh toán trước khi giao.");
            }
        }

        boolean isNewStatusCancelled = (newStatus == OrderStatus.CANCELLED);
        boolean isOldStatusCancelled = (oldStatus == OrderStatus.CANCELLED);

        if (isNewStatusCancelled && !isOldStatusCancelled) {
            List<OrderDetail> details = orderDetailRepository.findByOrder(order);
            for (OrderDetail item : details) {
                if (item.getProduct() != null && item.getProduct().getId() != null) {
                    Product lockedProduct = productRepository.findByIdWithLock(item.getProduct().getId()).orElse(null);
                    if (lockedProduct != null) {
                        int currentStock = lockedProduct.getStock() != null ? lockedProduct.getStock() : 0;
                        int quantityToReturn = item.getQuantity() != null ? item.getQuantity() : 0;
                        lockedProduct.setStock(currentStock + quantityToReturn);
                        productRepository.save(lockedProduct);
                        log.info("Hoàn tồn kho sản phẩm ID {} thêm {}", lockedProduct.getId(), quantityToReturn);
                    }
                }
            }
        }

        // Với hình thức COD, khi đơn hàng giao thành công (DELIVERED) thì tự động chuyển sang PAID vì shipper đã thu tiền
        if (order.getPaymentMode() == PaymentMode.COD && newStatus == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.PAID);
            log.info("Đơn hàng COD #{} đã giao thành công, tự động cập nhật trạng thái thanh toán thành PAID.", order.getId());
        }

        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        try {
            emailService.sendOrderStatusEmail(order.getEmailId(), order.getId(), newStatus.name());
        } catch (Exception e) {
            log.warn("Không thể gửi email thông báo trạng thái đơn hàng #{}: {}", order.getId(), e.getMessage());
        }
    }

    /**
     * Khách hàng chủ động hủy đơn khi chưa giao hàng (tự động hoàn tồn kho)
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderByCustomer(Integer orderId, String customerEmail) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + orderId));

        if (order.getEmailId() == null || !order.getEmailId().equalsIgnoreCase(customerEmail)) {
            throw new SecurityException("Bạn không có quyền hủy đơn hàng của người khác!");
        }

        OrderStatus currentStatus = order.getOrderStatus();
        if (currentStatus != OrderStatus.WAITING_FOR_PAYMENT && currentStatus != OrderStatus.PENDING) {
            throw new IllegalStateException("Đơn hàng đang ở trạng thái '" + currentStatus.name()
                    + "', đã được xử lý hoặc đang vận chuyển nên không thể tự hủy! Vui lòng liên hệ bộ phận hỗ trợ.");
        }

        updateOrderStatus(orderId, OrderStatus.CANCELLED);
        log.info("Khách hàng {} đã hủy thành công đơn hàng #{}", customerEmail, orderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Integer orderId, String newStatusStr) {
        updateOrderStatus(orderId, OrderStatus.fromString(newStatusStr));
    }

    @Transactional(readOnly = true)
    public Page<Order> getAllOrdersPaged(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public long countOrders() {
        return orderRepository.count();
    }

    public java.math.BigDecimal calculateTotalRevenue() {
        return orderRepository.calculateTotalRevenue();
    }

    public List<com.da.da.dto.DailyRevenueProjection> getDailyRevenueStatistics() {
        return orderRepository.getDailyRevenueStatistics();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public PaymentStatus getPaymentStatusForCustomer(Integer orderId, String email) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        if (order.getEmailId() == null || !order.getEmailId().equalsIgnoreCase(email)) {
            throw new SecurityException("Không có quyền truy cập");
        }
        return order.getPaymentStatus();
    }

    @Transactional(readOnly = true)
    public List<OrderDetail> getOrderDetails(Order order) {
        if (order == null) return List.of();
        return orderDetailRepository.findByOrder(order);
    }

    /**
     * Admin xác nhận thanh toán thủ công
     */
    @Transactional(rollbackFor = Exception.class)
    public Order confirmPaymentManually(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + orderId));

        order.setPaymentStatus(PaymentStatus.PAID);
        if (order.getOrderStatus() == OrderStatus.WAITING_FOR_PAYMENT) {
            order.setOrderStatus(OrderStatus.PENDING);
        }
        Order saved = orderRepository.save(order);

        try {
            if (order.getEmailId() != null && !order.getEmailId().isEmpty()) {
                emailService.sendOrderStatusEmail(order.getEmailId(), order.getId(), "Đã thanh toán thành công");
            }
        } catch (Exception e) {
            log.warn("Lỗi gửi email xác nhận thanh toán cho đơn hàng #{}: {}", order.getId(), e.getMessage());
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public String getOrdersForAi(String emailId) {
        List<Order> orders = orderRepository.findByEmailIdOrderByIdDesc(emailId);
        if (orders.isEmpty()) {
            return "Bạn chưa có đơn hàng nào tại cửa hàng.";
        }

        StringBuilder sb = new StringBuilder("Dưới đây là danh sách đơn hàng của bạn:\n");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (Order o : orders) {
            BigDecimal totalMoney = o.getProductTotalPrice() != null ? o.getProductTotalPrice() : BigDecimal.ZERO;
            sb.append(String.format("- Đơn hàng #%d | Trạng thái: %s | Tổng tiền: %s\n",
                    o.getId(), o.getOrderStatusName(), currencyFormat.format(totalMoney)));
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String countByStatusForAi(String status) {
        long count = orderRepository.countByOrderStatus(OrderStatus.fromString(status));
        return "Hiện có " + count + " đơn hàng đang ở trạng thái " + status;
    }
}