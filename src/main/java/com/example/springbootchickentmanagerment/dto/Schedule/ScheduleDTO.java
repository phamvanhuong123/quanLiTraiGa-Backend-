package com.example.springbootchickentmanagerment.dto.Schedule;

import com.example.springbootchickentmanagerment.enums.ScheduleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ScheduleDTO {
    private Long id;
    private Long flockId;
    private String flockName;
    private String title;
    private String description;
    private LocalDate scheduledDate;
    private ScheduleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}