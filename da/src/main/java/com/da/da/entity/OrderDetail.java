package com.da.da.entity;



import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "order_details") // Khớp với bảng trong SQL
@Data
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order; // Liên kết với đơn hàng tổng

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // Liên kết với sản phẩm

    @Column(name = "product_name")
    private String productName; // Lưu cứng tên SP (phòng khi admin đổi tên sau này)

    private Integer quantity;
    
    private Double price; // Giá tại thời điểm mua
    
    
    private BigDecimal totalPrice;
    
    public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}


    
}
