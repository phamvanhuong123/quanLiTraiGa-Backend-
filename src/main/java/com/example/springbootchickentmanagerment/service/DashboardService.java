package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.dashboard.DashboardStatsDTO;
import com.example.springbootchickentmanagerment.entity.InventoryBatch;
import com.example.springbootchickentmanagerment.enums.FlockStatus;
import com.example.springbootchickentmanagerment.enums.TransactionType;
import com.example.springbootchickentmanagerment.repository.FlockRepository;
import com.example.springbootchickentmanagerment.repository.InventoryBatchRepository;
import com.example.springbootchickentmanagerment.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    private FlockRepository flockRepository;

    @Autowired
    private InventoryBatchRepository inventoryBatchRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        // 1. Get total chickens currently raising
        Long totalChickens = Optional.ofNullable(flockRepository.sumCurrentQuantityByStatus(FlockStatus.RAISING)).orElse(0L);

        // 2. Get count of expiring batches
        LocalDate thresholdDate = LocalDate.now().plusDays(7);
        List<InventoryBatch> expiringBatchesList = inventoryBatchRepository.findExpiringBatches(thresholdDate);
        Integer expiringBatchesCount = expiringBatchesList.size();

        // 3. Calculate profit
        BigDecimal totalIncome = Optional.ofNullable(transactionRepository.sumAmountByType(TransactionType.INCOME)).orElse(BigDecimal.ZERO);
        BigDecimal totalExpense = Optional.ofNullable(transactionRepository.sumAmountByType(TransactionType.EXPENSE)).orElse(BigDecimal.ZERO);
        BigDecimal profit = totalIncome.subtract(totalExpense);

        return DashboardStatsDTO.builder()
                .totalChickens(totalChickens)
                .expiringBatches(expiringBatchesCount)
                .profit(profit)
                .build();
    }
}
