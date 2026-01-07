package com.da.da.repository;


import com.da.da.entity.Product;


import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 1. Chỉ cần extends JpaRepository là có sẵn:
    // - save() -> Lưu/Sửa
    // - findAll() -> Lấy tất cả
    // - deleteById() -> Xóa
    // - count() -> Đếm số lượng
    
    // 2. Các hàm tìm kiếm tùy chỉnh (nếu sau này cần):
    List<Product> findByProductCategory(String category); // Tìm theo danh mục
    
    // Tìm kiếm sản phẩm theo tên (Cho chức năng Search sau này)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.discountSold = p.discountSold + :quantity " +
           "WHERE p.id = :productId " +
           "AND p.discountLimit >= (p.discountSold + :quantity)") 
    int decreaseSaleSlot(Long productId, int quantity);
    
    Product findFirstByNameContainingIgnoreCase(String name);
    
    
    
}