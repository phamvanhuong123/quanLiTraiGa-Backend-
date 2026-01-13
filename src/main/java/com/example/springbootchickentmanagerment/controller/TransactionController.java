package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.transaction.TransactionRequest;
import com.example.springbootchickentmanagerment.dto.transaction.TransactionResponse;
import com.example.springbootchickentmanagerment.dto.transaction.TransactionSummaryResponse;
import com.example.springbootchickentmanagerment.enums.TransactionType;
import com.example.springbootchickentmanagerment.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ApiResponse<TransactionSummaryResponse> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category) {
        
        TransactionSummaryResponse summary = transactionService.getFilteredTransactions(startDate, endDate, type, category);
        return ApiResponse.<TransactionSummaryResponse>builder()
                .statusCode(200)
                .message("Success")
                .data(summary)
                .build();
    }

    @PostMapping
    public ApiResponse<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ApiResponse.<TransactionResponse>builder()
                .statusCode(201)
                .message("Transaction created successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<TransactionResponse> updateTransaction(@PathVariable Long id, @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.updateTransaction(id, request);
        return ApiResponse.<TransactionResponse>builder()
                .statusCode(200)
                .message("Transaction updated successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ApiResponse.<Void>builder()
                .statusCode(200)
                .message("Transaction deleted successfully")
                .build();
    }
}
