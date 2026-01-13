package com.example.springbootchickentmanagerment.dto.inventory;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InventoryBatchDTO {
    private Long id;
    private String materialName;
    private Long materialId;
    private String supplierName;
    private Long supplierId;
    private String batchCode;
    private Double quantityRemaining;
    private Double quantityImported;
    private LocalDate expiryDate;
    private LocalDate importDate;
    private BigDecimal pricePerUnit;
}
