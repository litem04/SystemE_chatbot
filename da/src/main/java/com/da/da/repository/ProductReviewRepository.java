package com.da.da.repository;

import com.da.da.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {
    // Lấy tất cả review của 1 sản phẩm
    List<ProductReview> findByProductId(Long productId);
}
