package com.da.da.entity.enums;

public enum OrderStatus {
    PENDING,
    WAITING_FOR_PAYMENT,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public static OrderStatus fromString(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Order status cannot be empty");
        }
        String normalized = status.trim().toUpperCase();
        if (normalized.contains("DELIVERED") || normalized.contains("ĐÃ GIAO") || normalized.contains("THÀNH CÔNG")) {
            return DELIVERED;
        }
        if (normalized.contains("SHIPPED") || normalized.contains("ĐANG GIAO")) {
            return SHIPPED;
        }
        if (normalized.contains("PROCESSING") || normalized.contains("ĐANG XỬ LÝ")) {
            return PROCESSING;
        }
        if (normalized.contains("CANCEL") || normalized.contains("HỦY")) {
            return CANCELLED;
        }
        if (normalized.contains("WAITING") || normalized.contains("CHỜ THANH TOÁN")) {
            return WAITING_FOR_PAYMENT;
        }
        return OrderStatus.valueOf(normalized);
    }
}
