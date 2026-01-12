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
    
    List<InventoryBatch> findByMaterialIdAndQuantityRemainingGreaterThanOrderByExpiryDateAsc(Long materialId, Double quantity);
    
    @Query("SELECT b FROM InventoryBatch b WHERE b.expiryDate < :thresholdDate AND b.quantityRemaining > 0")
    List<InventoryBatch> findExpiringBatches(@Param("thresholdDate") LocalDate thresholdDate);
    
    @Query("SELECT COALESCE(SUM(b.quantityRemaining), 0) FROM InventoryBatch b WHERE b.material.id = :materialId")
    Double getTotalRemainingQuantityByMaterialId(@Param("materialId") Long materialId);
    
    @Query("SELECT b FROM InventoryBatch b WHERE b.material.id = :materialId AND b.quantityRemaining > 0 ORDER BY b.expiryDate ASC")
    List<InventoryBatch> findAvailableBatchesByMaterialIdOrderByExpiryDateAsc(@Param("materialId") Long materialId);
    
    @Query("SELECT COUNT(b) FROM InventoryBatch b WHERE b.expiryDate <= :expiryThreshold AND b.quantityRemaining > 0")
    long countExpiringBatches(@Param("expiryThreshold") LocalDate expiryThreshold);
}