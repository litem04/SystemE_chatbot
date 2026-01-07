package com.da.da.service.tool;

import com.da.da.service.OrderService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserOrderTools {

    @Autowired
    private OrderService orderService;

    
  
    @Tool("Tra cứu danh sách và trạng thái các đơn hàng của người dùng hiện tại")
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
}