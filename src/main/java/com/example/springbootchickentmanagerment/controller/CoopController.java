package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.inventory.CoopDTO;
import com.example.springbootchickentmanagerment.service.CoopService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coops")
public class CoopController {

    @Autowired
    private CoopService coopService;

    @PostMapping
    public ResponseEntity<ApiResponse<CoopDTO>> createCoop(@Valid @RequestBody CoopDTO coopDTO) {
        CoopDTO createdCoop = coopService.createCoop(coopDTO);
        ApiResponse<CoopDTO> response = ApiResponse.<CoopDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Coop created successfully")
                .data(createdCoop)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CoopDTO>>> getAllCoops() {
        List<CoopDTO> coops = coopService.getAllCoops();
        ApiResponse<List<CoopDTO>> response = ApiResponse.<List<CoopDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Coops retrieved successfully")
                .data(coops)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CoopDTO>> getCoopById(@PathVariable Long id) {
        CoopDTO coop = coopService.getCoopById(id);
        ApiResponse<CoopDTO> response = ApiResponse.<CoopDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Coop retrieved successfully")
                .data(coop)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/empty")
    public ResponseEntity<ApiResponse<List<CoopDTO>>> getEmptyCoops() {
        List<CoopDTO> emptyCoops = coopService.getEmptyCoops();
        ApiResponse<List<CoopDTO>> response = ApiResponse.<List<CoopDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Empty coops retrieved successfully")
                .data(emptyCoops)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CoopDTO>> updateCoop(@PathVariable Long id, @Valid @RequestBody CoopDTO coopDTO) {
        CoopDTO updatedCoop = coopService.updateCoop(id, coopDTO);
        ApiResponse<CoopDTO> response = ApiResponse.<CoopDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Coop updated successfully")
                .data(updatedCoop)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoop(@PathVariable Long id) {
        coopService.deleteCoop(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Coop deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}