package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.inventory.ImportMaterialDTO;
import com.example.springbootchickentmanagerment.dto.inventory.InventoryBatchDTO;
import com.example.springbootchickentmanagerment.entity.*;
import com.example.springbootchickentmanagerment.enums.TransactionType;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private InventoryBatchRepository inventoryBatchRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void importMaterial(ImportMaterialDTO dto) {
        // 1. Get Current User
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        // 2. Validate Material & Supplier
        Material material = materialRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Material not found"));

        Supplier supplier = null;
        if (dto.getSupplierId() != null) {
            supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Supplier not found"));
        }

        // 3. Create Inventory Batch
        InventoryBatch batch = InventoryBatch.builder()
                .material(material)
                .supplier(supplier)
                .batchCode("MAT-" + System.currentTimeMillis())
                .quantityImported(dto.getQuantity())
                .quantityRemaining(dto.getQuantity())
                .pricePerUnit(dto.getPricePerUnit())
                .importDate(LocalDate.now())
                .expiryDate(dto.getExpiryDate())
                .createdBy(currentUser)
                .build();

        inventoryBatchRepository.save(batch);

        // 4. Create Transaction (Expense)
        BigDecimal totalAmount = dto.getPricePerUnit().multiply(BigDecimal.valueOf(dto.getQuantity()));

        Transaction transaction = Transaction.builder()
                .transactionDate(LocalDate.now())
                .type(TransactionType.EXPENSE)
                .category("Nhập vật tư")
                .amount(totalAmount)
                .description("Nhập kho " + material.getName() + " - Lô " + batch.getBatchCode())
                .createdBy(currentUser)
                .build();

        transactionRepository.save(transaction);
    }

    public List<InventoryBatchDTO> getExpiringBatches() {
        LocalDate thresholdDate = LocalDate.now().plusDays(7);
        List<InventoryBatch> batches = inventoryBatchRepository.findExpiringBatches(thresholdDate);

        return batches.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private InventoryBatchDTO mapToDTO(InventoryBatch batch) {
        return InventoryBatchDTO.builder()
                .id(batch.getId())
                .materialName(batch.getMaterial().getName())
                .batchCode(batch.getBatchCode())
                .quantityRemaining(batch.getQuantityRemaining())
                .expiryDate(batch.getExpiryDate())
                .importDate(batch.getImportDate())
                .pricePerUnit(batch.getPricePerUnit())
                .build();
    }
}
