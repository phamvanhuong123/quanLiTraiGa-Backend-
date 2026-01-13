package com.example.springbootchickentmanagerment.dto.log;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DailyLogCreateDTO {
    @NotNull(message = "Flock ID is required")
    private Long flockId;

    @NotNull(message = "Log date is required")
    private LocalDate logDate;

    @Min(value = 0, message = "Mortality must be greater than or equal to 0")
    private Integer mortality;

    @Min(value = 0, message = "Cull must be greater than or equal to 0")
    private Integer cull;

    private String notes;

    private List<DailyLogDetailCreateDTO> details;
}