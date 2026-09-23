package com.da.da.service;

import com.da.da.entity.Cart;
import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.entity.Product;
import com.da.da.repository.CartRepository;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * Xử lý đặt hàng toàn diện theo chuẩn ACID (Atomic, Consistent, Isolated, Durable)
     */
    @Transactional(rollbackFor = Exception.class)
    public Order placeOrder(Customer user, String address, String phone, String paymentMode) {
        if (user == null) {
            throw new IllegalArgumentException("Người dùng chưa đăng nhập.");
        }

        List<Cart> cartItems = cartRepository.findByCustomerId(Long.valueOf(user.getId()));
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng của bạn đang trống.");
        }

        // 1. Khởi tạo đơn hàng
        Order order = Order.builder()
                .customerName(user.getName())
                .emailId(user.getEmail())
                .mobileNumber(phone)
                .address(address)
                .orderDate(new Date())
                .paymentMode(paymentMode)
                .paymentStatus("Unpaid")
                .orderStatus("VIETQR".equalsIgnoreCase(paymentMode) ? "WAITING_FOR_PAYMENT" : "PENDING")
                .productTotalPrice(BigDecimal.ZERO)
                .build();

        Order savedOrder = orderRepository.save(order);
        BigDecimal grandTotal = BigDecimal.ZERO;

        // 2. Duyệt qua từng sản phẩm trong giỏ để trừ kho và tạo OrderDetail
        for (Cart item : cartItems) {
            Product product = item.getProduct();
            if (product == null) {
                cartRepository.delete(item);
                continue;
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
            productRepository.save(product);

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
    public void updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + orderId));

        String oldStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim() : "";
        String targetStatus = newStatus != null ? newStatus.trim() : "";

        boolean isNewStatusCancelled = "CANCELLED".equalsIgnoreCase(targetStatus) || "Hủy".equalsIgnoreCase(targetStatus);
        boolean isOldStatusCancelled = "CANCELLED".equalsIgnoreCase(oldStatus) || "Hủy".equalsIgnoreCase(oldStatus);

        if (isNewStatusCancelled && !isOldStatusCancelled) {
            List<OrderDetail> details = orderDetailRepository.findByOrder(order);
            for (OrderDetail item : details) {
                Product product = item.getProduct();
                if (product != null) {
                    int currentStock = product.getStock() != null ? product.getStock() : 0;
                    int quantityToReturn = item.getQuantity() != null ? item.getQuantity() : 0;
                    product.setStock(currentStock + quantityToReturn);
                    productRepository.save(product);
                    log.info("Hoàn tồn kho sản phẩm ID {} thêm {}", product.getId(), quantityToReturn);
                }
            }
        }

        order.setOrderStatus(targetStatus);
        orderRepository.save(order);

        try {
            emailService.sendOrderStatusEmail(order.getEmailId(), order.getId(), targetStatus);
        } catch (Exception e) {
            log.warn("Không thể gửi email thông báo trạng thái đơn hàng #{}: {}", order.getId(), e.getMessage());
        }
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
                    o.getId(), o.getOrderStatus(), currencyFormat.format(totalMoney)));
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String countByStatusForAi(String status) {
        long count = orderRepository.countByOrderStatusIgnoreCase(status);
        return "Hiện có " + count + " đơn hàng đang ở trạng thái " + status;
    }
}