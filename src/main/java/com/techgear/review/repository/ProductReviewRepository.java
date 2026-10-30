package com.techgear.review.repository;

import com.techgear.review.entity.ProductReview;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {
    List<ProductReview> findByProductId(Long productId);

    List<ProductReview> findByProductIdOrderByDateDesc(Long productId);

    @Query("SELECT AVG(CAST(r.rating AS double)) FROM ProductReview r WHERE r.product.id = :productId")
    Double getAverageRating(@org.springframework.data.repository.query.Param("productId") Long productId);

    Long countByProductId(Long productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductReview p WHERE p.product.id = :productId")
    void deleteByProductId(Long productId);
}
