package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.inventory.AvailableSupplyDTO;
import com.example.springbootchickentmanagerment.dto.inventory.ImportMaterialDTO;
import com.example.springbootchickentmanagerment.dto.inventory.InventoryBatchDTO;
import com.example.springbootchickentmanagerment.dto.inventory.MaterialStockDTO;
import com.example.springbootchickentmanagerment.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<Void>> importMaterial(@RequestBody ImportMaterialDTO dto) {
        inventoryService.importMaterial(dto);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Material imported successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<InventoryBatchDTO>>> getExpiringBatches() {
        List<InventoryBatchDTO> batches = inventoryService.getExpiringBatches();
        ApiResponse<List<InventoryBatchDTO>> response = ApiResponse.<List<InventoryBatchDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Expiring batches retrieved successfully")
                .data(batches)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-materials")
    public ResponseEntity<ApiResponse<List<MaterialStockDTO>>> getAvailableMaterials() {
        List<MaterialStockDTO> materials = inventoryService.getAvailableMaterialsWithStock();
        ApiResponse<List<MaterialStockDTO>> response = ApiResponse.<List<MaterialStockDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Available materials retrieved successfully")
                .data(materials)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-supplies")
    public ResponseEntity<ApiResponse<List<AvailableSupplyDTO>>> getAvailableSupplies(
            @RequestParam(required = false) String materialType) {
        List<AvailableSupplyDTO> supplies = inventoryService.getAvailableSupplies(materialType);
        ApiResponse<List<AvailableSupplyDTO>> response = ApiResponse.<List<AvailableSupplyDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Đã lấy danh sách vật tư có sẵn thành công")
                .data(supplies)
                .build();
        return ResponseEntity.ok(response);
    }
}
