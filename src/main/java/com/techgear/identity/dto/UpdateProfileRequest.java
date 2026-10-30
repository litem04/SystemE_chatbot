package com.techgear.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    private String email;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String name;

    @Pattern(regexp = "^(0|\\+84)?[0-9]{9,10}$|^$", message = "Số điện thoại không hợp lệ (9-10 chữ số)")
    private String phone;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    private String gender;

    @Size(max = 20, message = "Mã bưu chính không được vượt quá 20 ký tự")
    private String pinCode;
}
