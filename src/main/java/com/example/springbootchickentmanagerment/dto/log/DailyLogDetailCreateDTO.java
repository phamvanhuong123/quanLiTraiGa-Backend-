package com.example.springbootchickentmanagerment.dto.log;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DailyLogDetailCreateDTO {
    @NotNull(message = "Inventory batch ID is required")
    private Long inventoryBatchId;

    @NotNull(message = "Quantity used is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private Double quantityUsed;
}