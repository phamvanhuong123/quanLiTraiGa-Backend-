package com.example.springbootchickentmanagerment.dto.flock;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FlockImportDTO {
    private Long breedId;
    private Long supplierId;
    private Long coopId;
    private Integer quantity;
    private BigDecimal pricePerChick;
    private LocalDate importDate;
}
