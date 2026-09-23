package com.da.da.service.tool;

import com.da.da.entity.Order;
import com.da.da.repository.OrderRepository;
import com.da.da.service.OrderService;
import com.da.da.service.PaymentService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

@Component
public class UserOrderTools {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    public UserOrderTools(OrderService orderService,
                          PaymentService paymentService,
                          OrderRepository orderRepository) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth.getName();
    }

    @Tool("Lấy lịch sử đơn hàng của người dùng đang đăng nhập hiện tại")
    public String getMyOrderHistory() {
        String emailId = getCurrentUserEmail();
        if (emailId == null) {
            return "Người dùng chưa đăng nhập. Hãy nhắc khách hàng đăng nhập để xem thông tin đơn hàng.";
        }
        return orderService.getOrdersForAi(emailId);
    }

    @Tool("Lấy mã QR thanh toán cho đơn hàng đang chờ thanh toán mới nhất của người dùng hiện tại")
    public String getOrderPaymentQR() {
        String emailId = getCurrentUserEmail();
        if (emailId == null) {
            return "Bạn vui lòng đăng nhập vào tài khoản để lấy mã QR thanh toán cho đơn hàng của mình.";
        }

        Optional<Order> unpaidOrderOpt = orderRepository.findFirstByEmailIdAndPaymentStatusOrderByIdDesc(emailId, "Unpaid");
        if (unpaidOrderOpt.isEmpty()) {
            return "Bạn không có đơn hàng nào đang chờ thanh toán.";
        }

        Order order = unpaidOrderOpt.get();
        String qrUrl = paymentService.getVietQRUrl(order);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = order.getProductTotalPrice() != null ? currencyFormat.format(order.getProductTotalPrice()) : "0 đ";

        return String.format("Mã QR thanh toán cho đơn hàng #%d của bạn (Tổng tiền: %s):\n%s",
                order.getId(), formattedPrice, qrUrl);
    }
}