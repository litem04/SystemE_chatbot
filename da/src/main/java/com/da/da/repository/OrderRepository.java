package com.da.da.repository;

import com.da.da.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByEmailIdOrderByIdDesc(String emailId);

    List<Order> findByOrderStatus(String orderStatus);

    List<Order> findByPaymentStatusOrderByIdDesc(String paymentStatus);

    // Lấy đơn hàng chưa thanh toán mới nhất của đúng user đang đăng nhập (chống IDOR)
    Optional<Order> findFirstByEmailIdAndPaymentStatusOrderByIdDesc(String emailId, String paymentStatus);

    // Tính tổng doanh thu trực tiếp từ Database
    @Query("SELECT COALESCE(SUM(o.productTotalPrice), 0) FROM Order o WHERE LOWER(o.orderStatus) IN ('delivered', 'đã giao', 'thành công')")
    BigDecimal calculateTotalRevenue();

    // Đếm số đơn theo trạng thái
    long countByOrderStatusIgnoreCase(String orderStatus);
}