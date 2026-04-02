package com.da.da.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "tblorders") // Map vào bảng tblorders
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_no")
    private Integer orderNo;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "email_id")
    private String emailId;

    private String address;

    @Column(name = "address_type")
    private String addressType;

    private String pincode;
    private String image;

    @Column(name = "product_name")
    private String productName;

    private Integer quantity;

    @Column(name = "product_price")
    private String productPrice;

    @Column(name = "product_selling_price")
    private String productSellingPrice;

    @Column(name = "product_total_price")
    private String productTotalPrice;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "payment_id")
    private Integer paymentId;
    
	 // Trước giờ bạn chỉ có status đơn hàng (Pending, Shipped...	), 
	 // giờ cần thêm status tiền nong.
	 private String paymentStatus; // Giá trị ví dụ: "Paid" (Đã trả), "Unpaid" (Chưa trả)
	 
	 
	 public String getPaymentStatus() {
		return paymentStatus;
	}

	 public void setPaymentStatus(String paymentStatus) {
		 this.paymentStatus = paymentStatus;
	 }

	 public String getTransactionId() {
		 return transactionId;
	 }

	 public void setTransactionId(String transactionId) {
		 this.transactionId = transactionId;
	 }

	 // 2. Mã giao dịch VNPay (Để sau này đối soát nếu cần)
	 // Khi VNPay trả về, họ sẽ gửi kèm 1 mã giao dịch dài ngoằng, nên lưu lại.
	 private String transactionId;
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderNo() {
		return orderNo;
	}
	
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	public String getProductSellingPrice() {
		return productSellingPrice;
	}

	public void setProductSellingPrice(String productSellingPrice) {
		this.productSellingPrice = productSellingPrice;
	}

	public String getProductTotalPrice() {
		return productTotalPrice;
	}

	public void setProductTotalPrice(String productTotalPrice) {
		this.productTotalPrice = productTotalPrice;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
    
    
}
