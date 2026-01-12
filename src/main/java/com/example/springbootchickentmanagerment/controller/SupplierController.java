package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.entity.Supplier;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Supplier>> createSupplier(@Valid @RequestBody Supplier supplier) {
        Supplier createdSupplier = supplierService.createSupplier(supplier);
        ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Supplier created successfully")
                .data(createdSupplier)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Supplier>>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        ApiResponse<List<Supplier>> response = ApiResponse.<List<Supplier>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Suppliers retrieved successfully")
                .data(suppliers)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Supplier>> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.getSupplierById(id);
        ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Supplier retrieved successfully")
                .data(supplier)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Supplier>> updateSupplier(@PathVariable Long id, @Valid @RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.updateSupplier(id, supplier);
        ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Supplier updated successfully")
                .data(updatedSupplier)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Supplier deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}