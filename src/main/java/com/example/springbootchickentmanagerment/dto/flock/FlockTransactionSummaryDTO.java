package com.example.springbootchickentmanagerment.dto.flock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlockTransactionSummaryDTO {
    private Long flockId;
    private List<FlockTransactionDTO> transactions;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal materialCost;
    private BigDecimal totalCost;
    private BigDecimal netProfit;
}