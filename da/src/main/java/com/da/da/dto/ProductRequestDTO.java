package com.da.da.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class ProductRequestDTO {
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", message = "Giá không được nhỏ hơn 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Giá MRP không được nhỏ hơn 0")
    private BigDecimal mrpPrice;

    @NotBlank(message = "Danh mục không được để trống")
    private String productCategory;

    @DecimalMin(value = "0.0", message = "Giá khuyến mãi không được nhỏ hơn 0")
    private BigDecimal discountPrice;

    @Min(value = 0, message = "Giới hạn khuyến mãi không hợp lệ")
    private Integer discountLimit;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được nhỏ hơn 0")
    private Integer stock;
}
