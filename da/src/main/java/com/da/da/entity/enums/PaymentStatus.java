package com.da.da.entity.enums;

public enum PaymentStatus {
    PAID,
    UNPAID,
    REFUNDED;

    public static PaymentStatus fromString(String status) {
        if (status == null || status.isBlank()) {
            return UNPAID;
        }
        String normalized = status.trim().toUpperCase();
        if (normalized.contains("UNPAID") || normalized.contains("CHƯA")) {
            return UNPAID;
        }
        if (normalized.contains("REFUND")) {
            return REFUNDED;
        }
        if (normalized.contains("PAID") || normalized.contains("ĐÃ TRẢ") || normalized.contains("ĐÃ THANH TOÁN")) {
            return PAID;
        }
        return UNPAID;
    }
}
