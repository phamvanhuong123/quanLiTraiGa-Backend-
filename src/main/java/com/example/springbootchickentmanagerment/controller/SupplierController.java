package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.supplier.SupplierDTO;
import com.example.springbootchickentmanagerment.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    // Lấy danh sách
    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierDTO>>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllSupplier();
        ApiResponse<List<SupplierDTO>> response = ApiResponse.<List<SupplierDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy thông tin thành công")
                .data(suppliers)
                .build();
        return ResponseEntity.ok(response);
    }

    // Lấy thông tin chi tiết 1 nhà cung cấp
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDTO>> getSupplierById(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.getSupplierById(id);
        ApiResponse<SupplierDTO> response = ApiResponse.<SupplierDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lấy thông tin chi tiết thành công")
                .data(supplier)
                .build();
        return ResponseEntity.ok(response);
    }

    // Tạo thông tin nhà cung cấp
    @PostMapping
    public ResponseEntity<ApiResponse<SupplierDTO>> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO){
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        ApiResponse<SupplierDTO> response = ApiResponse.<SupplierDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Thêm thành công")
                .data(createdSupplier)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Cập nhật thông tin nhà cung cấp
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDTO>> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        ApiResponse<SupplierDTO> response = ApiResponse.<SupplierDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cập nhật thành công")
                .data(updatedSupplier)
                .build();
        return ResponseEntity.ok(response);
    }

    // Xóa nhà cung cấp
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Xóa thành công")
                .build();
        return ResponseEntity.ok(response);
    }
}
