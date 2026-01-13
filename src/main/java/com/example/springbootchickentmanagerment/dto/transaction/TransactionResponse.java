package com.example.springbootchickentmanagerment.dto.transaction;

import com.example.springbootchickentmanagerment.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private LocalDate transactionDate;
    private TransactionType type;
    private String category;
    private BigDecimal amount;
    private String description;
    private String flockName;
    private String createdByFullName;
}
