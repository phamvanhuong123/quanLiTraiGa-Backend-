package com.example.springbootchickentmanagerment.dto.inventory;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AvailableSupplyDTO {
    private Long batchId;
    private Long materialId;
    private String materialName;
    private String materialType;
    private String unit;
    private String batchCode;
    private LocalDate expiryDate;
    private Double quantityRemaining;
    private BigDecimal pricePerUnit;
}