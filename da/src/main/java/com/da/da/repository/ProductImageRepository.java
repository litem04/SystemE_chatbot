package com.da.da.repository;


import com.da.da.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // Spring sẽ tự động xử lý mọi việc lưu/xóa cho bạn
}