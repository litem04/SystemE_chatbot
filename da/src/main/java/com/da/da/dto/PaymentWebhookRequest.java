package com.da.da.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentWebhookRequest {
    private String description;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    @Positive(message = "Số tiền thanh toán phải lớn hơn 0")
    private BigDecimal amount;
    private String transactionId;
    
    private String reference;

    @jakarta.validation.constraints.AssertTrue(message = "Phải có transactionId hoặc reference")
    public boolean isTransactionIdOrReferenceValid() {
        return (transactionId != null && !transactionId.trim().isEmpty()) ||
               (reference != null && !reference.trim().isEmpty());
    }
}
