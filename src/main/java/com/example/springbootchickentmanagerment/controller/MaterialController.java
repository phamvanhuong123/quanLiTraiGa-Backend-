package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.inventory.MaterialDTO;
import com.example.springbootchickentmanagerment.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaterialDTO>> createMaterial(@Valid @RequestBody MaterialDTO materialDTO) {
        MaterialDTO createdMaterial = materialService.createMaterial(materialDTO);
        ApiResponse<MaterialDTO> response = ApiResponse.<MaterialDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Material created successfully")
                .data(createdMaterial)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaterialDTO>>> getAllMaterials() {
        List<MaterialDTO> materials = materialService.getAllMaterials();
        ApiResponse<List<MaterialDTO>> response = ApiResponse.<List<MaterialDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Materials retrieved successfully")
                .data(materials)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialDTO>> getMaterialById(@PathVariable Long id) {
        MaterialDTO material = materialService.getMaterialById(id);
        ApiResponse<MaterialDTO> response = ApiResponse.<MaterialDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Material retrieved successfully")
                .data(material)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialDTO>> updateMaterial(@PathVariable Long id, @Valid @RequestBody MaterialDTO materialDTO) {
        MaterialDTO updatedMaterial = materialService.updateMaterial(id, materialDTO);
        ApiResponse<MaterialDTO> response = ApiResponse.<MaterialDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Material updated successfully")
                .data(updatedMaterial)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Material deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
