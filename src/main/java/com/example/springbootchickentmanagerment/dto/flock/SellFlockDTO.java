package com.example.springbootchickentmanagerment.dto.flock;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SellFlockDTO {
    private Long flockId;
    private Integer soldQuantity;
    private Double totalWeight; // Tổng cân nặng bán được
    private BigDecimal totalPrice; // Tổng số tiền thu được
}
