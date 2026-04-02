package com.da.da.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblproduct") // Map vào bảng tblproduct
@Data
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
    
    
 // Thêm dòng này vào trong class Product
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> productImages;
    public List<ProductImage> getProductImages() {
        return productImages;
    }
    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
    }
    
    @Column(name = "image_name")
    private String imageName;

    private String name;
    private Double price;     // DB bạn lưu varchar
    
    @Column(name = "mrp_price")
    private Double mrpPrice;  // DB bạn lưu varchar

    @Column(name = "product_category")
    private String productCategory;
    
    @Column(name = "discount_price")
    private Double discountPrice; 

    // Tổng số lượng cho phép bán giá giảm (Ví dụ: 10 cái)
    @Column(name = "discount_limit")
    private Integer discountLimit; 

    // Số lượng đã bán được với giá giảm (Ban đầu set là 0)
    @Column(name = "discount_sold")
    private Integer discountSold;
    
    private Integer stock;
    	
    
    
	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getMrpPrice() {
		return mrpPrice;
	}

	public void setMrpPrice(Double mrpPrice) {
		this.mrpPrice = mrpPrice;
	}

	public Double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public Integer getDiscountLimit() {
		return discountLimit;
	}

	public void setDiscountLimit(Integer discountLimit) {
		this.discountLimit = discountLimit;
	}

	public Integer getDiscountSold() {
		return discountSold;
	}

	public void setDiscountSold(Integer discountSold) {
		this.discountSold = discountSold;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	
    
}
