package com.da.da.dto;

import java.math.BigDecimal;

public interface DailyRevenueProjection {
    String getOrderDay();
    BigDecimal getTotalAmount();
}
