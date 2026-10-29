package com.da.da.dto;

import java.math.BigDecimal;

public interface TopSellingProductProjection {
    Long getProductId();
    String getProductName();
    Long getTotalSold();
    BigDecimal getTotalRevenue();
}
