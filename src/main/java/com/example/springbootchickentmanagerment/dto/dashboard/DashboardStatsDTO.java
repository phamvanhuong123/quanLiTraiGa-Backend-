package com.example.springbootchickentmanagerment.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatsDTO {
    private Long totalChickens;
    private Integer expiringBatches;
    private BigDecimal profit;
}
