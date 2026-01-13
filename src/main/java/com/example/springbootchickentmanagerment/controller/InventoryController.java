package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.inventory.AvailableSupplyDTO;
import com.example.springbootchickentmanagerment.dto.inventory.ImportMaterialDTO;
import com.example.springbootchickentmanagerment.dto.inventory.InventoryBatchDTO;
import com.example.springbootchickentmanagerment.dto.inventory.MaterialStockDTO;
import com.example.springbootchickentmanagerment.service.InventoryService;
import jakarta.validation.Valid;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryBatchDTO>>> getAllInventoryBatches(){
        List<InventoryBatchDTO> inventoryBatchDTOList = inventoryService.getAll();
        ApiResponse<List<InventoryBatchDTO>> response = ApiResponse.<List<InventoryBatchDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách thành công")
                .data(inventoryBatchDTOList)
                .build();
        return ResponseEntity.ok(response);

    }
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<InventoryBatchDTO>> updateInventory(@PathVariable Long id, @RequestBody @Valid InventoryBatchDTO inventoryBatchDTO){
        InventoryBatchDTO batchDTO = inventoryService.updateInventory(id, inventoryBatchDTO);
        ApiResponse<InventoryBatchDTO> response = ApiResponse.<InventoryBatchDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cập nhật thành công")
                .data(batchDTO)
                .build();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/import")
    public ResponseEntity<ApiResponse<Void>> importMaterial(@RequestBody ImportMaterialDTO dto) {
        inventoryService.importMaterial(dto);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Vật tư đã được nhập vào kho thành công")
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
