package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.flock.FlockImportDTO;
import com.example.springbootchickentmanagerment.dto.flock.SellFlockDTO;
import com.example.springbootchickentmanagerment.service.FlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flocks")
public class FlockController {

    @Autowired
    private FlockService flockService;

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<Void>> importFlock(@RequestBody FlockImportDTO flockImportDTO) {
        flockService.importFlock(flockImportDTO);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Flock imported successfully and schedules created.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<Void>> sellFlock(@RequestBody SellFlockDTO sellFlockDTO) {
        flockService.sellFlock(sellFlockDTO);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Flock sold and transaction recorded successfully.")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<Void>> closeFlock(@PathVariable Long id) {
        flockService.closeFlock(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Flock closed and coop status updated successfully.")
                .build();
        return ResponseEntity.ok(response);
    }
}
