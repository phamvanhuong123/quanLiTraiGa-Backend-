package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.log.DailyLogCreateDTO;
import com.example.springbootchickentmanagerment.dto.log.DailyLogDetailCreateDTO;
import com.example.springbootchickentmanagerment.dto.log.DailyLogResponseDTO;
import com.example.springbootchickentmanagerment.entity.*;
import com.example.springbootchickentmanagerment.enums.FlockStatus;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyLogService {

    @Autowired
    private DailyLogRepository dailyLogRepository;
    @Autowired
    private DailyLogDetailRepository dailyLogDetailRepository;
    @Autowired
    private FlockRepository flockRepository;
    @Autowired
    private InventoryBatchRepository inventoryBatchRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Transactional
    public void createDailyLog(DailyLogCreateDTO dto) {
        // 1. Lấy user hiện tại
        User currentUser = getCurrentUser();

        // 2. Tìm Flock và kiểm tra
        Flock flock = flockRepository.findById(dto.getFlockId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Flock not found"));

        if (flock.getStatus() != FlockStatus.RAISING) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Cannot add daily log to non-raising flock");
        }

        // 3. Kiểm tra mortality và cull
        int totalReduction = (dto.getMortality() != null ? dto.getMortality() : 0) +
                (dto.getCull() != null ? dto.getCull() : 0);
        int newQuantity = flock.getCurrentQuantity() - totalReduction;

        if (newQuantity < 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST,
                    "Mortality and cull count cannot exceed current flock quantity.");
        }

        // 4. Cập nhật số lượng đàn
        flock.setCurrentQuantity(newQuantity);
        flockRepository.save(flock);

        // 5. Tạo DailyLog
        DailyLog dailyLog = DailyLog.builder()
                .flock(flock)
                .logDate(dto.getLogDate())
                .mortality(dto.getMortality())
                .cull(dto.getCull())
                .notes(dto.getNotes())
                .createdBy(currentUser)
                .build();

        DailyLog savedDailyLog = dailyLogRepository.save(dailyLog);

        // 6. Xử lý chi tiết vật tư tiêu hao (dùng inventoryBatchId)
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (DailyLogDetailCreateDTO detail : dto.getDetails()) {
                handleMaterialUsage(detail, savedDailyLog);
            }
        }
    }

    private void handleMaterialUsage(DailyLogDetailCreateDTO detail, DailyLog savedDailyLog) {
        double quantityToUse = detail.getQuantityUsed();
        if (quantityToUse <= 0)
            return;

        // Tìm lô hàng cụ thể bằng inventoryBatchId
        InventoryBatch batch = inventoryBatchRepository.findById(detail.getInventoryBatchId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                        "Inventory batch not found with ID: " + detail.getInventoryBatchId()));

        // Kiểm tra số lượng tồn kho
        if (batch.getQuantityRemaining() < quantityToUse) {
            throw new CustomException(HttpStatus.BAD_REQUEST,
                    String.format("Not enough stock in batch %s. Required: %.2f, Available: %.2f",
                            batch.getBatchCode(), quantityToUse, batch.getQuantityRemaining()));
        }

        // Cập nhật số lượng tồn kho
        batch.setQuantityRemaining(batch.getQuantityRemaining() - quantityToUse);
        inventoryBatchRepository.save(batch);

        // Tạo DailyLogDetail
        DailyLogDetail logDetail = DailyLogDetail.builder()
                .dailyLog(savedDailyLog)
                .inventoryBatch(batch)
                .quantityUsed(quantityToUse)
                .build();
        dailyLogDetailRepository.save(logDetail);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        String subject = authentication.getName();

        // Thử tìm bằng email trước
        User currentUser = userRepository.findByEmail(subject)
                .orElseGet(() -> {
                    // Nếu không tìm thấy bằng email, thử tìm bằng username
                    return userRepository.findByUsername(subject)
                            .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED,
                                    "User not found"));
                });

        return currentUser;
    }

    public List<DailyLogResponseDTO> getDailyLogsByFlockId(Long flockId) {
        List<DailyLog> logs = dailyLogRepository.findByFlockId(flockId);

        return logs.stream().map(log -> {
            DailyLogResponseDTO dto = new DailyLogResponseDTO();
            dto.setId(log.getId());
            dto.setFlockId(log.getFlock().getId());
            dto.setFlockName(log.getFlock().getBatchCode() + " - " + log.getFlock().getName());
            dto.setLogDate(log.getLogDate());
            dto.setMortality(log.getMortality());
            dto.setCull(log.getCull());
            dto.setNotes(log.getNotes());
            dto.setCreatedAt(log.getCreatedAt());
            dto.setCreatedBy(log.getCreatedBy() != null ? log.getCreatedBy().getFullName() : null);

            // Map details
            List<DailyLogResponseDTO.DailyLogDetailDTO> detailDTOs = log.getDetails().stream()
                    .map(detail -> {
                        DailyLogResponseDTO.DailyLogDetailDTO detailDTO = new DailyLogResponseDTO.DailyLogDetailDTO();
                        detailDTO.setId(detail.getId());
                        detailDTO.setInventoryBatchId(detail.getInventoryBatch().getId());
                        detailDTO.setBatchCode(detail.getInventoryBatch().getBatchCode());
                        detailDTO.setMaterialName(detail.getInventoryBatch().getMaterial().getName());
                        detailDTO.setQuantityUsed(detail.getQuantityUsed());
                        detailDTO.setPricePerUnit(detail.getInventoryBatch().getPricePerUnit());
                        return detailDTO;
                    })
                    .collect(Collectors.toList());
            dto.setDetails(detailDTOs);

            return dto;
        }).collect(Collectors.toList());
    }

    public BigDecimal calculateTotalMaterialCostForFlock(Long flockId) {
        List<DailyLog> logs = dailyLogRepository.findByFlockId(flockId);

        BigDecimal totalCost = BigDecimal.ZERO;
        for (DailyLog log : logs) {
            for (DailyLogDetail detail : log.getDetails()) {
                BigDecimal quantity = BigDecimal.valueOf(detail.getQuantityUsed());
                BigDecimal price = detail.getInventoryBatch().getPricePerUnit();
                totalCost = totalCost.add(quantity.multiply(price));
            }
        }

        return totalCost;
    }

    public boolean checkMaterialAvailability(Long materialId, Double requiredQuantity) {
        Double availableStock = inventoryBatchRepository.getTotalRemainingQuantityByMaterialId(materialId);
        return availableStock != null && availableStock >= requiredQuantity;
    }

    public List<Material> getAvailableMaterials() {
        return materialRepository.findAll().stream()
                .filter(material -> {
                    Double stock = inventoryBatchRepository.getTotalRemainingQuantityByMaterialId(material.getId());
                    return stock != null && stock > 0;
                })
                .collect(Collectors.toList());
    }
}