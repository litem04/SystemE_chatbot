package com.da.da.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblproduct")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String active;
    private String code;

    @Column(name = "create_date")
    private Date createDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 1000)
    private String image;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> productImages;

    @Column(name = "image_name")
    private String imageName;

    private String name;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "mrp_price", precision = 15, scale = 2)
    private BigDecimal mrpPrice;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "discount_price", precision = 15, scale = 2)
    private BigDecimal discountPrice;

    @Column(name = "discount_limit")
    private Integer discountLimit;

    @Column(name = "discount_sold")
    private Integer discountSold;

    private Integer stock;

    /**
     * Lấy giá thực tế áp dụng (nếu có giá giảm hợp lệ thì lấy giá giảm, ngược lại lấy giá gốc)
     */
    public BigDecimal getEffectivePrice() {
        if (discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0) {
            return discountPrice;
        }
        return price != null ? price : BigDecimal.ZERO;
    }
}
