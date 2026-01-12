package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.inventory.*;
import com.example.springbootchickentmanagerment.service.BreedService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breeds")
public class BreedController {

    @Autowired
    private BreedService breedService;

    @PostMapping
    public ResponseEntity<ApiResponse<BreedDTO>> createBreed(@Valid @RequestBody BreedDTO breedDTO) {
        BreedDTO createdBreed = breedService.createBreed(breedDTO);
        ApiResponse<BreedDTO> response = ApiResponse.<BreedDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Breed created successfully")
                .data(createdBreed)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BreedDTO>>> getAllBreeds() {
        List<BreedDTO> breeds = breedService.getAllBreeds();
        ApiResponse<List<BreedDTO>> response = ApiResponse.<List<BreedDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Breeds retrieved successfully")
                .data(breeds)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BreedDTO>> getBreedById(@PathVariable Long id) {
        BreedDTO breed = breedService.getBreedById(id);
        ApiResponse<BreedDTO> response = ApiResponse.<BreedDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Breed retrieved successfully")
                .data(breed)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BreedDTO>> updateBreed(@PathVariable Long id, @Valid @RequestBody BreedDTO breedDTO) {
        BreedDTO updatedBreed = breedService.updateBreed(id, breedDTO);
        ApiResponse<BreedDTO> response = ApiResponse.<BreedDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Breed updated successfully")
                .data(updatedBreed)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBreed(@PathVariable Long id) {
        breedService.deleteBreed(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Breed deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}