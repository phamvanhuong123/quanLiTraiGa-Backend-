package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.InventoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {

    // Find batches for a specific material that still have stock, ordered by expiration date (FIFO)
    List<InventoryBatch> findByMaterialIdAndQuantityRemainingGreaterThanOrderByExpiryDateAsc(Long materialId, Double quantity);

    @Query("SELECT b FROM InventoryBatch b WHERE b.expiryDate < :thresholdDate AND b.quantityRemaining > 0")
    List<InventoryBatch> findExpiringBatches(@Param("thresholdDate") LocalDate thresholdDate);
}
