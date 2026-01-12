package com.example.springbootchickentmanagerment.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreedDTO {
    private Long id;
    private String name;
    private Double targetWeight;
    private Integer maturityDays;
}