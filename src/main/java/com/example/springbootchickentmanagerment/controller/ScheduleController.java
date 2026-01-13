package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.Schedule.CreateScheduleDTO;
import com.example.springbootchickentmanagerment.dto.Schedule.ScheduleDTO;
import com.example.springbootchickentmanagerment.dto.Schedule.ScheduleStats;
import com.example.springbootchickentmanagerment.entity.Schedule;
import com.example.springbootchickentmanagerment.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // Tạo schedule mới (thủ công)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<ScheduleDTO>> createSchedule(
            @Valid @RequestBody CreateScheduleDTO dto) {
        Schedule schedule = scheduleService.createSchedule(dto);
        ScheduleDTO responseDTO = convertToDTO(schedule);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ScheduleDTO>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Tạo lịch trình thành công")
                        .data(responseDTO)
                        .build());
    }

    // Cập nhật schedule
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<ScheduleDTO>> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody CreateScheduleDTO dto) {
        Schedule schedule = scheduleService.updateSchedule(id, dto);
        ScheduleDTO responseDTO = convertToDTO(schedule);

        return ResponseEntity.ok(ApiResponse.<ScheduleDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cập nhật lịch trình thành công")
                .data(responseDTO)
                .build());
    }

    // Xóa schedule
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Xóa lịch trình thành công")
                .build());
    }

    // Lấy schedule theo flockId
    @GetMapping("/flock/{flockId}")
    public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getSchedulesByFlockId(@PathVariable Long flockId) {
        List<Schedule> schedules = scheduleService.getSchedulesByFlockId(flockId);
        List<ScheduleDTO> dtos = schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<ScheduleDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách lịch trình thành công")
                .data(dtos)
                .build());
    }

    // Lấy schedule quá hạn
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getOverdueSchedules() {
        List<Schedule> schedules = scheduleService.getOverdueSchedules();
        List<ScheduleDTO> dtos = schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<ScheduleDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách lịch trình quá hạn thành công")
                .data(dtos)
                .build());
    }

    // Lấy thống kê schedule
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ScheduleStats>> getScheduleStats() {
        ScheduleStats stats = scheduleService.getScheduleStats();
        return ResponseEntity.ok(ApiResponse.<ScheduleStats>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy thống kê lịch trình thành công")
                .data(stats)
                .build());
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getTodaySchedules() {
        List<Schedule> schedules = scheduleService.getTodaySchedules();
        List<ScheduleDTO> dtos = schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<ScheduleDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy lịch trình hôm nay thành công")
                .data(dtos)
                .build());
    }

    @PostMapping("/{scheduleId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeSchedule(@PathVariable Long scheduleId) {
        scheduleService.completeSchedule(scheduleId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Hoàn thành lịch trình thành công")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{scheduleId}/skip")
    public ResponseEntity<ApiResponse<Void>> skipSchedule(@PathVariable Long scheduleId) {
        scheduleService.skipSchedule(scheduleId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Bỏ qua lịch trình thành công")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getUpcomingSchedules(
            @RequestParam(defaultValue = "7") int days) {
        List<Schedule> schedules = scheduleService.getUpcomingSchedules(days);
        List<ScheduleDTO> dtos = schedules.stream()
                .map(this::convertToDTO) // CHUYỂN ĐỔI SANG DTO
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<ScheduleDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy lịch trình sắp tới thành công")
                .data(dtos)
                .build());
    }

    private ScheduleDTO convertToDTO(Schedule schedule) {
        return ScheduleDTO.builder()
                .id(schedule.getId())
                .flockId(schedule.getFlock() != null ? schedule.getFlock().getId() : null)
                .flockName(schedule.getFlock() != null ? schedule.getFlock().getName() : null)
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .scheduledDate(schedule.getScheduledDate())
                .status(schedule.getStatus())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}