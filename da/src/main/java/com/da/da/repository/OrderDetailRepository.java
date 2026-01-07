package com.da.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail	, Long>{
	List<OrderDetail> findByOrder(Order order);
	

}
