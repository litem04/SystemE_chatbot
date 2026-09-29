package com.da.da.entity.enums;

public enum PaymentMode {
    COD,
    VIETQR,
    MOMO,
    BANKING;

    public static PaymentMode fromString(String mode) {
        if (mode == null || mode.isBlank()) {
            throw new IllegalArgumentException("Payment mode cannot be empty");
        }
        return PaymentMode.valueOf(mode.trim().toUpperCase());
    }

    public String getDisplayName() {
        return switch (this) {
            case COD -> "Thanh toán khi nhận hàng (COD)";
            case VIETQR -> "Chuyển khoản VietQR";
            case MOMO -> "Ví MoMo";
            case BANKING -> "Chuyển khoản ngân hàng";
        };
    }
}
