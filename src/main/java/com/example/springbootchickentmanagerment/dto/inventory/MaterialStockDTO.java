package com.example.springbootchickentmanagerment.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialStockDTO {
    private Long id;
    private String name;
    private String type;
    private String unit;
    private Double totalQuantity;
    private boolean hasStock;
}