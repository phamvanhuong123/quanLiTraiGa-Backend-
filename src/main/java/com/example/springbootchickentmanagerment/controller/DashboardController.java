package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.dashboard.DashboardStatsDTO;
import com.example.springbootchickentmanagerment.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        ApiResponse<DashboardStatsDTO> response = ApiResponse.<DashboardStatsDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Dashboard statistics retrieved successfully.")
                .data(stats)
                .build();
        return ResponseEntity.ok(response);
    }
}
