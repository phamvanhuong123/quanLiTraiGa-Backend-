package com.example.springbootchickentmanagerment.dto.flock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FlockImportDTO {
    
    @NotBlank(message = "Tên đàn không được để trống")
    private String name;
    
    @NotNull(message = "Giống gà không được để trống")
    private Long breedId;
    
    @NotNull(message = "Nhà cung cấp không được để trống")
    private Long supplierId;
    
    @NotNull(message = "Chuồng không được để trống")
    private Long coopId;
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
    
    @NotNull(message = "Giá con giống không được để trống")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal pricePerChick;
    
    @NotNull(message = "Ngày nhập không được để trống")
    private LocalDate importDate;
}