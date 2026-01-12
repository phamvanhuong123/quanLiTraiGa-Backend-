package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.Schedule.ScheduleDTO;
import com.example.springbootchickentmanagerment.entity.Schedule;
import com.example.springbootchickentmanagerment.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/flocks/{flockId}/schedules") 
    public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getSchedulesByFlockId(@PathVariable Long flockId) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByFlockId(flockId);

            // Convert to DTO
            List<ScheduleDTO> scheduleDTOs = schedules.stream()
                    .map(schedule -> {
                        // Kiểm tra null cho flock
                        Long flockIdValue = schedule.getFlock() != null ? schedule.getFlock().getId() : null;
                        String flockName = schedule.getFlock() != null ? schedule.getFlock().getName() : "Unknown";

                        return ScheduleDTO.builder()
                                .id(schedule.getId())
                                .flockId(flockIdValue)
                                .flockName(flockName)
                                .title(schedule.getTitle())
                                .scheduledDate(schedule.getScheduledDate())
                                .status(schedule.getStatus())
                                .build();
                    })
                    .collect(Collectors.toList());

            ApiResponse<List<ScheduleDTO>> response = ApiResponse.<List<ScheduleDTO>>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách lịch trình thành công")
                    .data(scheduleDTOs)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<List<ScheduleDTO>> response = ApiResponse.<List<ScheduleDTO>>builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi lấy lịch trình: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/schedules/today")
    public ResponseEntity<ApiResponse<List<Schedule>>> getTodaySchedules() {
        List<Schedule> schedules = scheduleService.getTodaySchedules();
        ApiResponse<List<Schedule>> response = ApiResponse.<List<Schedule>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy lịch trình hôm nay thành công")
                .data(schedules)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/schedules/{scheduleId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeSchedule(@PathVariable Long scheduleId) {
        scheduleService.completeSchedule(scheduleId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Hoàn thành lịch trình thành công")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/schedules/{scheduleId}/skip")
    public ResponseEntity<ApiResponse<Void>> skipSchedule(@PathVariable Long scheduleId) {
        scheduleService.skipSchedule(scheduleId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Bỏ qua lịch trình thành công")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedules/upcoming")
    public ResponseEntity<ApiResponse<List<Schedule>>> getUpcomingSchedules(
            @RequestParam(defaultValue = "7") int days) {
        List<Schedule> schedules = scheduleService.getUpcomingSchedules(days);
        ApiResponse<List<Schedule>> response = ApiResponse.<List<Schedule>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy lịch trình sắp tới thành công")
                .data(schedules)
                .build();
        return ResponseEntity.ok(response);
    }
}