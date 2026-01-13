package com.example.springbootchickentmanagerment.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long flockId;
    private String flockName;
    private String type;
    private String category;
    private Double amount;
    private String description;
    private String transactionDate;
    private String createdBy;
    // Getters and setters
}
