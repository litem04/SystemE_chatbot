package com.da.da.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.da.da.entity.Cart;

import jakarta.transaction.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	List<Cart>findByCustomerId(Long customerId);
	
	Cart findByCustomerIdAndProductId( Long customerId, Long productId);
	
	// Tìm nhanh bằng Email (Rất quan trọng cho AI integration)
    // Giả sử trong Entity Cart của bạn có quan hệ với Entity Customer/User
    
    @Modifying
    @Transactional
    void deleteByCustomerIdAndProductId(Long customerId, Long productId);

    // Xóa toàn bộ giỏ hàng (Sau khi chốt đơn thành công)
    @Modifying
    @Transactional
    void deleteByCustomerId(Long customerId);
	
}
