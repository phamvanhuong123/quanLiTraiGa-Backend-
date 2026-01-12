package com.example.springbootchickentmanagerment.dto.flock;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlockProfitDTO {
    private Long flockId;
    private String flockName;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal feedAndMedicineCost;
    private BigDecimal totalCost;
    private BigDecimal profit;
}
