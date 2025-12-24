package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.log.DailyLogCreateDTO;
import com.example.springbootchickentmanagerment.service.DailyLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class DailyLogController {

    @Autowired
    private DailyLogService dailyLogService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createDailyLog(@RequestBody DailyLogCreateDTO dailyLogCreateDTO) {
        dailyLogService.createDailyLog(dailyLogCreateDTO);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Daily log created successfully.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
