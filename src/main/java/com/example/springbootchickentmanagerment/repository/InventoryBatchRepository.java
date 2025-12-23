package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.InventoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {
    List<InventoryBatch> findByMaterialIdAndQuantityRemainingGreaterThanOrderByExpiryDateAsc(Long materialId, Double quantity);
}
