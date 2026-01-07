package com.da.da.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tblcart") // Map vào bảng tblcart
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discount_price")
    private String discountPrice;

    private Integer quantity;

    @Column(name = "total_price")
    private String totalPrice;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne // Một sản phẩm có thể nằm trong nhiều giỏ hàng
    @JoinColumn(name = "product_id", insertable = false, updatable = false) // Chỉ để đọc thông tin
    private Product product;

    @Column(name = "product_id")
    private Long productId; // Giữ lại cái này để code thêm giỏ hàng cũ vẫn chạy

    @Column(name = "mrp_price")
    private String mrpPrice;

	public String getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(String discountPrice) {
		this.discountPrice = discountPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getMrpPrice() {
		return mrpPrice;
	}

	public void setMrpPrice(String mrpPrice) {
		this.mrpPrice = mrpPrice;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
    
}