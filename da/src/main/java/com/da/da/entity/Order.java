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
    private String image;

    @Column(name = "product_name")
    private String productName;

    private Integer quantity;

    @Column(name = "product_price", precision = 15, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "product_selling_price", precision = 15, scale = 2)
    private BigDecimal productSellingPrice;

    @Column(name = "product_total_price", precision = 15, scale = 2)
    private BigDecimal productTotalPrice;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "payment_status")
    private String paymentStatus; // "Paid", "Unpaid"

    @Column(name = "transaction_id")
    private String transactionId;

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
