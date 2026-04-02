package com.da.da.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.da.da.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
  
    List<Order> findByEmailIdOrderByIdDesc(String emailId);

    // Tìm đơn theo trạng thái (chúng ta sẽ lấy list về rồi tự cộng trong Java)
    // Lưu ý: order_status phải đúng tên cột trong DB, nếu lỗi báo tôi sửa lại tên biến
    List<Order> findByOrderStatus(String orderStatus);
    
    List<Order> findByPaymentStatusOrderByIdDesc(String paymentStatus);
}