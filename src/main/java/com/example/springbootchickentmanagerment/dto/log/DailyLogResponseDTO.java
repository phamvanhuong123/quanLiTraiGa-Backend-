package com.example.springbootchickentmanagerment.dto.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyLogResponseDTO {
    private Long id;
    private Long flockId;
    private String flockName;
    private LocalDate logDate;
    private Integer mortality;
    private Integer cull;
    private String notes;
    private LocalDateTime createdAt;
    private String createdBy;
    private List<DailyLogDetailDTO> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyLogDetailDTO {
        private Long id;
        private Long inventoryBatchId;
        private String batchCode;
        private String materialName;
        private Double quantityUsed;
        private BigDecimal pricePerUnit;
    }
}