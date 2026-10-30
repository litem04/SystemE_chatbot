package com.techgear.admin.dto;

import java.math.BigDecimal;

public interface DailyRevenueProjection {
    String getOrderDay();
    BigDecimal getTotalAmount();
}
