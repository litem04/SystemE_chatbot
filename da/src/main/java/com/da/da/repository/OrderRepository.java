package com.da.da.repository;

import com.da.da.dto.DailyRevenueProjection;
import com.da.da.entity.Order;
import com.da.da.entity.enums.OrderStatus;
import com.da.da.entity.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Khóa bi quan (Pessimistic Write Lock) chống Race Condition khi webhook gọi đồng thời
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithLock(@Param("id") Integer id);

    List<Order> findByEmailIdOrderByIdDesc(String emailId);

    Page<Order> findByEmailIdOrderByIdDesc(String emailId, Pageable pageable);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    List<Order> findByPaymentStatusOrderByIdDesc(PaymentStatus paymentStatus);

    // Lấy đơn hàng chưa thanh toán mới nhất của đúng user đang đăng nhập (chống IDOR)
    Optional<Order> findFirstByEmailIdAndPaymentStatusOrderByIdDesc(String emailId, PaymentStatus paymentStatus);

    Optional<Order> findByTransactionId(String transactionId);

    // Tính tổng doanh thu trực tiếp từ Database
    @Query("SELECT COALESCE(SUM(o.productTotalPrice), 0) FROM Order o WHERE o.orderStatus = com.da.da.entity.enums.OrderStatus.DELIVERED")
    BigDecimal calculateTotalRevenue();

    // Thống kê doanh thu theo ngày từ Database thay vì tải toàn bộ orders lên RAM
    @Query(value = "SELECT TO_CHAR(o.order_date, 'DD/MM') AS orderDay, COALESCE(SUM(o.product_total_price), 0) AS totalAmount " +
                   "FROM tblorders o " +
                   "WHERE UPPER(o.order_status) IN ('DELIVERED', 'ĐÃ GIAO', 'THÀNH CÔNG') " +
                   "GROUP BY TO_CHAR(o.order_date, 'DD/MM'), DATE(o.order_date) " +
                   "ORDER BY DATE(o.order_date) ASC", nativeQuery = true)
    List<DailyRevenueProjection> getDailyRevenueStatistics();

    // Đếm số đơn theo trạng thái
    long countByOrderStatus(OrderStatus orderStatus);
}