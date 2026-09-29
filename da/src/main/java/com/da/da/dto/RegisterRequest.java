package com.da.da.dto;

import jakarta.validation.constraints.Email;
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
public class RegisterRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private String mobileNumber;

    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 50, message = "Mật khẩu phải có từ 6 đến 50 ký tự")
    private String password;

    private String gender;

    private String address;

    private String addressType;

    private String pincode;

    private String pinCode;

    public String getEffectiveMobileNumber() {
        return (mobileNumber != null && !mobileNumber.isBlank()) ? mobileNumber : phone;
    }

    public String getEffectivePincode() {
        return (pincode != null && !pincode.isBlank()) ? pincode : pinCode;
    }
}
