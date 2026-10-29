package com.da.da.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "tblorders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    // === CÁC FIELD CŨ (LEGACY) - GIỮ LẠI ĐỂ TƯƠNG THÍCH SCHEMA DB CŨ ===
    // Chi tiết sản phẩm, giá từng món đã được chuẩn hóa chuyển sang thực thể OrderDetail.
    @Deprecated
    private String image;

    @Deprecated
    @Column(name = "product_name")
    private String productName;

    @Deprecated
    private Integer quantity;

    @Deprecated
    @Column(name = "product_price", precision = 15, scale = 2)
    private BigDecimal productPrice;

    @Deprecated
    @Column(name = "product_selling_price", precision = 15, scale = 2)
    private BigDecimal productSellingPrice;

    // Tổng tiền toàn bộ đơn hàng (tổng của các OrderDetail)
    @Column(name = "product_total_price", precision = 15, scale = 2)
    private BigDecimal productTotalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private com.da.da.entity.enums.OrderStatus orderStatus;

    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private com.da.da.entity.enums.PaymentMode paymentMode;

    @Column(name = "payment_id")
    private Integer paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private com.da.da.entity.enums.PaymentStatus paymentStatus;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    public void setOrderStatus(String status) {
        this.orderStatus = com.da.da.entity.enums.OrderStatus.fromString(status);
    }

    public void setOrderStatus(com.da.da.entity.enums.OrderStatus status) {
        this.orderStatus = status;
    }

    public void setPaymentStatus(String status) {
        this.paymentStatus = com.da.da.entity.enums.PaymentStatus.fromString(status);
    }

    public void setPaymentStatus(com.da.da.entity.enums.PaymentStatus status) {
        this.paymentStatus = status;
    }

    public void setPaymentMode(String mode) {
        this.paymentMode = com.da.da.entity.enums.PaymentMode.fromString(mode);
    }

    public void setPaymentMode(com.da.da.entity.enums.PaymentMode mode) {
        this.paymentMode = mode;
    }

    public boolean isPaid() {
        return this.paymentStatus == com.da.da.entity.enums.PaymentStatus.PAID;
    }

    public String getOrderStatusName() {
        return orderStatus != null ? orderStatus.name() : "";
    }

    public String getPaymentStatusName() {
        return paymentStatus != null ? paymentStatus.name() : "";
    }

    public String getPaymentModeName() {
        return paymentMode != null ? paymentMode.name() : "";
    }

    /**
     * Backward-compatible alias for totalAmount
     */
    public BigDecimal getTotalAmount() {
        return productTotalPrice != null ? productTotalPrice : BigDecimal.ZERO;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.productTotalPrice = totalAmount;
    }
}
