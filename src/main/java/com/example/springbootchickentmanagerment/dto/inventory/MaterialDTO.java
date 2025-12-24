package com.example.springbootchickentmanagerment.dto.inventory;

import com.example.springbootchickentmanagerment.enums.MaterialType;
import lombok.Data;

@Data
public class MaterialDTO {
    private Long id;
    private String name;
    private String unit;
    private MaterialType type;
}
