package com.da.da.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderRequest {

    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    @Size(min = 5, max = 255, message = "Địa chỉ nhận hàng phải từ 5 đến 255 ký tự")
    private String address;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
    @Pattern(regexp = "(?i)^(COD|VIETQR|VNPAY)$", message = "Phương thức thanh toán không hợp lệ (hỗ trợ COD, VIETQR, VNPAY)")
    private String paymentMode;
}
