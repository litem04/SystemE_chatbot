package com.da.da.service.tool;

import com.da.da.entity.Order;
import com.da.da.repository.OrderRepository;
import com.da.da.service.OrderService;
import com.da.da.service.PaymentService;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserOrderTools {

    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;
  
    @Tool("Lấy lịch sử đơn hàng của người dùng. Kết quả trả về gồm mã đơn, trạng thái và số tiền tổng cộng của mỗi đơn hàng của người dùng hiện tại")
    public String getMyOrderHistory() {
    			
        // 1. Lấy thông tin định danh từ Spring Security Session
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User hiện tại: " + auth.getName());
        // 2. Kiểm tra xem khách đã đăng nhập chưa
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "Người dùng chưa đăng nhập. Hãy nhắc khách hàng đăng nhập để xem thông tin này.";
        }

        // 3. Lấy emailId (thường là username trong Spring Security)
        String emailId = auth.getName();

        // 4. Gọi hàm trong OrderService mà chúng ta vừa thống nhất ở bước trước
        return orderService.getOrdersForAi(emailId);
    }
    @Tool("Lấy mã QR thanh toán cho đơn hàng mới nhất của khách hàng")
    public String getOrderPaymentQR() { // Bỏ tham số orderId đi để AI không truyền bậy
    	System.out.println(">>> AI ĐANG GỌI HÀM getOrderPaymentQR RỒI NÈ BỒ!");
        // 1. Lấy thông tin user đang chat từ Session hoặc từ context của bạn
        // (Tôi giả định bạn có cách lấy customerId của người đang chat, ví dụ từ 1 biến global hoặc session)
        // Nếu chưa có, bạn có thể tạm thời lấy Đơn hàng UNPAID mới nhất trong DB:
        
        List<Order> unpaidOrders = orderRepository.findByPaymentStatusOrderByIdDesc("Unpaid");
        
        if (unpaidOrders.isEmpty()) {
             return "Bạn không có đơn hàng nào đang chờ thanh toán cả bồ ơi!";
        }
        
        // Lấy đơn hàng Unpaid mới nhất
        Order order = unpaidOrders.get(0); 

        // Gọi lại service tạo link QR (nhớ bọc Markdown ![] như tôi hướng dẫn ở câu trước nhé)
        String qrUrl = paymentService.getVietQRUrl(order);
        
        return "Mã QR thanh toán cho đơn hàng #" + order.getId() + " của bạn đây bồ. " +
               "Tổng tiền: " + order.getProductTotalPrice() + " đ. " + qrUrl;
    }
}