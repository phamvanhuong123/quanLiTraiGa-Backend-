package com.example.springbootchickentmanagerment.dto.flock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SellFlockDTO {
    
    @NotNull(message = "Mã đàn không được để trống")
    private Long flockId;
    
    @NotNull(message = "Số lượng bán không được để trống")
    @Min(value = 1, message = "Số lượng bán phải lớn hơn 0")
    private Integer soldQuantity;
    
    @NotNull(message = "Tổng trọng lượng không được để trống")
    @Min(value = 0, message = "Trọng lượng phải lớn hơn hoặc bằng 0")
    private Double totalWeight;
    
    @NotNull(message = "Tổng tiền không được để trống")
    @Min(value = 0, message = "Số tiền phải lớn hơn hoặc bằng 0")
    private BigDecimal totalPrice;
    
    private LocalDate transactionDate;
    
    private boolean closeFlock = false;
}