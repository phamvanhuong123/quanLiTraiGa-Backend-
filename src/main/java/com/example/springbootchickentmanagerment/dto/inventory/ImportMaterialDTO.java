package com.example.springbootchickentmanagerment.dto.inventory;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ImportMaterialDTO {
    private Long materialId;
    private Long supplierId;
    private Double quantity;
    private BigDecimal pricePerUnit;
    private LocalDate expiryDate;
}
