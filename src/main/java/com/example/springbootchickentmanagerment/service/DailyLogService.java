package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.log.DailyLogCreateDTO;
import com.example.springbootchickentmanagerment.dto.log.MaterialUsageDTO;
import com.example.springbootchickentmanagerment.entity.*;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public void createDailyLog(DailyLogCreateDTO dto) {
        // 1. Get Current User
        User currentUser = getCurrentUser();

        // 2. Find Flock and update chicken quantity
        Flock flock = flockRepository.findById(dto.getFlockId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Flock not found"));

        int totalReduction = (dto.getMortality() != null ? dto.getMortality() : 0) + (dto.getCull() != null ? dto.getCull() : 0);
        int newQuantity = flock.getCurrentQuantity() - totalReduction;
        if (newQuantity < 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Mortality and cull count cannot exceed current flock quantity.");
        }
        flock.setCurrentQuantity(newQuantity);
        flockRepository.save(flock);

        // 3. Create and save the main DailyLog entry
        DailyLog dailyLog = DailyLog.builder()
                .flock(flock)
                .logDate(dto.getLogDate())
                .mortality(dto.getMortality())
                .cull(dto.getCull())
                .notes(dto.getNotes())
                .createdBy(currentUser)
                .build();
        DailyLog savedDailyLog = dailyLogRepository.save(dailyLog);

        // 4. Process material usage with FIFO logic
        if (dto.getMaterials() != null && !dto.getMaterials().isEmpty()) {
            for (MaterialUsageDTO usage : dto.getMaterials()) {
                handleMaterialUsage(usage, savedDailyLog);
            }
        }
    }

    private void handleMaterialUsage(MaterialUsageDTO usage, DailyLog savedDailyLog) {
        double quantityToUse = usage.getQuantityUsed();
        if (quantityToUse <= 0) return;

        // a. Find available batches for the material, ordered by FIFO (expiryDate ASC)
        List<InventoryBatch> batches = inventoryBatchRepository.findByMaterialIdAndQuantityRemainingGreaterThanOrderByExpiryDateAsc(usage.getMaterialId(), 0.0);

        // b. Loop through batches to fulfill the required quantity
        for (InventoryBatch batch : batches) {
            double availableInBatch = batch.getQuantityRemaining();
            double quantityFromThisBatch;

            if (quantityToUse <= availableInBatch) {
                // This batch is enough
                quantityFromThisBatch = quantityToUse;
                batch.setQuantityRemaining(availableInBatch - quantityToUse);
                quantityToUse = 0;
            } else {
                // This batch is not enough, use all of it and continue to the next
                quantityFromThisBatch = availableInBatch;
                batch.setQuantityRemaining(0.0); // Corrected: Use 0.0 for Double
                quantityToUse -= availableInBatch;
            }

            // c. Update the batch in the database
            inventoryBatchRepository.save(batch);

            // d. Create a detail record for this usage
            DailyLogDetail detail = DailyLogDetail.builder()
                    .dailyLog(savedDailyLog)
                    .inventoryBatch(batch) // Corrected: Use the correct field name
                    .quantityUsed(quantityFromThisBatch)
                    .build();
            dailyLogDetailRepository.save(detail);

            if (quantityToUse == 0) {
                break; // The required quantity has been fulfilled
            }
        }

        // e. If after checking all batches, we still need more, throw an error
        if (quantityToUse > 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Not enough stock for material ID: " + usage.getMaterialId() + ". Required: " + usage.getQuantityUsed() + ", but only " + (usage.getQuantityUsed() - quantityToUse) + " was available.");
        }
    }

    private User getCurrentUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
