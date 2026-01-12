package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.log.DailyLogCreateDTO;
import com.example.springbootchickentmanagerment.dto.log.DailyLogResponseDTO;
import com.example.springbootchickentmanagerment.service.DailyLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/daily-logs")
@Tag(name = "Daily Log Management", description = "APIs for managing daily flock logs")
public class DailyLogController {

    @Autowired
    private DailyLogService dailyLogService;

    @PostMapping
    @Operation(summary = "Create a new daily log")
    public ResponseEntity<ApiResponse<Void>> createDailyLog(@Valid @RequestBody DailyLogCreateDTO dailyLogCreateDTO) {
        dailyLogService.createDailyLog(dailyLogCreateDTO);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Daily log created successfully.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/flock/{flockId}")
    @Operation(summary = "Get all daily logs for a flock")
    public ResponseEntity<ApiResponse<List<DailyLogResponseDTO>>> getDailyLogsByFlock(@PathVariable Long flockId) {
        List<DailyLogResponseDTO> logs = dailyLogService.getDailyLogsByFlockId(flockId);
        ApiResponse<List<DailyLogResponseDTO>> response = ApiResponse.<List<DailyLogResponseDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Daily logs retrieved successfully.")
                .data(logs)
                .build();
        return ResponseEntity.ok(response);
    }
}