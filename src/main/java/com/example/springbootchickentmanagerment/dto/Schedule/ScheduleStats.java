package com.example.springbootchickentmanagerment.dto.Schedule;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleStats {
    private Long totalSchedules;
    private Long pendingSchedules;
    private Long doneSchedules;
    private Long skippedSchedules;
    private Long overdueSchedules;
}