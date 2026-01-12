package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.inventory.ImportMaterialDTO;
import com.example.springbootchickentmanagerment.dto.inventory.InventoryBatchDTO;
import com.example.springbootchickentmanagerment.dto.inventory.MaterialStockDTO;
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
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                // 1. Get Current User từ Security Context
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new CustomException(HttpStatus.UNAUTHORIZED, "User not authenticated");
                }

                // Lấy subject từ token (có thể là email)
                String subject = authentication.getName();
                System.out.println("Authentication subject: " + subject);

                // Thử tìm user bằng email trước (vì trong JWT subject là email)
                User currentUser = userRepository.findByEmail(subject)
                                .orElseGet(() -> {
                                        // Nếu không tìm thấy bằng email, thử tìm bằng username
                                        return userRepository.findByUsername(subject)
                                                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                                                        "User not found with identifier: " + subject));
                                });

                // 2. Validate Material
                Material material = materialRepository.findById(dto.getMaterialId())
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Material not found"));

                // 3. Validate Supplier (có thể null)
                Supplier supplier = null;
                if (dto.getSupplierId() != null) {
                        supplier = supplierRepository.findById(dto.getSupplierId())
                                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                                        "Supplier not found"));
                }

                // 4. Tạo mã lô
                String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                String batchCode = "MAT-" + material.getId() + "-" + timestamp + "-" + System.currentTimeMillis();

                // 5. Tạo Inventory Batch
                InventoryBatch batch = InventoryBatch.builder()
                                .material(material)
                                .supplier(supplier)
                                .batchCode(batchCode)
                                .quantityImported(dto.getQuantity())
                                .quantityRemaining(dto.getQuantity())
                                .pricePerUnit(dto.getPricePerUnit())
                                .importDate(LocalDate.now())
                                .expiryDate(dto.getExpiryDate())
                                .createdBy(currentUser)
                                .build();

                inventoryBatchRepository.save(batch);

                // 6. Tạo Transaction (Expense)
                BigDecimal totalAmount = dto.getPricePerUnit().multiply(BigDecimal.valueOf(dto.getQuantity()));

                Transaction transaction = Transaction.builder()
                                .transactionDate(LocalDate.now())
                                .type(TransactionType.EXPENSE)
                                .category("Nhập vật tư")
                                .amount(totalAmount)
                                .description(String.format("Nhập kho %s - Lô %s, Số lượng: %.2f %s",
                                                material.getName(), batchCode, dto.getQuantity(), material.getUnit()))
                                .createdBy(currentUser)
                                .build();

                transactionRepository.save(transaction);
        }

        public List<InventoryBatchDTO> getExpiringBatches() {
                LocalDate thresholdDate = LocalDate.now().plusDays(7);
                List<InventoryBatch> batches = inventoryBatchRepository.findExpiringBatches(thresholdDate);

                return batches.stream().map(this::mapToDTO).collect(Collectors.toList());
        }

        public List<MaterialStockDTO> getAvailableMaterialsWithStock() {
                List<Material> materials = materialRepository.findAll();

                return materials.stream()
                                .map(material -> {
                                        Double totalStock = inventoryBatchRepository
                                                        .getTotalRemainingQuantityByMaterialId(material.getId());
                                        return MaterialStockDTO.builder()
                                                        .id(material.getId())
                                                        .name(material.getName())
                                                        .type(material.getType().name())
                                                        .unit(material.getUnit())
                                                        .totalQuantity(totalStock != null ? totalStock : 0.0)
                                                        .hasStock(totalStock != null && totalStock > 0)
                                                        .build();
                                })
                                .collect(Collectors.toList());
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
