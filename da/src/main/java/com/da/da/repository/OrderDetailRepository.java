package com.da.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	List<OrderDetail> findByOrder(Order order);

	@org.springframework.data.jpa.repository.Query("SELECT COUNT(od) > 0 FROM OrderDetail od WHERE od.order.emailId = :email AND od.product.id = :productId AND od.order.orderStatus = com.da.da.entity.enums.OrderStatus.DELIVERED")
	boolean hasPurchasedDeliveredProduct(@org.springframework.data.repository.query.Param("email") String email,
										 @org.springframework.data.repository.query.Param("productId") Long productId);

	@org.springframework.data.jpa.repository.Query("SELECT od.product.id AS productId, od.productName AS productName, SUM(od.quantity) AS totalSold, SUM(od.totalPrice) AS totalRevenue " +
		   "FROM OrderDetail od " +
		   "WHERE od.order.orderStatus = com.da.da.entity.enums.OrderStatus.DELIVERED " +
		   "GROUP BY od.product.id, od.productName " +
		   "ORDER BY SUM(od.quantity) DESC")
	List<com.da.da.dto.TopSellingProductProjection> findTopSellingProducts(org.springframework.data.domain.Pageable pageable);
}
