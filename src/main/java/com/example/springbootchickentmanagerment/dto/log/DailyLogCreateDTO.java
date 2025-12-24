package com.example.springbootchickentmanagerment.dto.log;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DailyLogCreateDTO {
    private Long flockId;
    private LocalDate logDate;
    private Integer mortality;
    private Integer cull;
    private String notes;
    private List<MaterialUsageDTO> materials;
}
