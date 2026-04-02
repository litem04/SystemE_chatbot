package com.da.da.repository;

import com.da.da.entity.ProductReview;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {
    // Lấy tất cả review của 1 sản phẩm
    List<ProductReview> findByProductId(Long productId);
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductReview p WHERE p.product.id = :productId")
    void deleteByProductId(Long productId);
}
