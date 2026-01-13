package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.flock.*;
import com.example.springbootchickentmanagerment.dto.log.DailyLogResponseDTO;
import com.example.springbootchickentmanagerment.entity.Flock;
import com.example.springbootchickentmanagerment.entity.Transaction;
import com.example.springbootchickentmanagerment.enums.TransactionType;
import com.example.springbootchickentmanagerment.service.DailyLogService;
import com.example.springbootchickentmanagerment.service.FlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flocks")
@Tag(name = "Flock Management", description = "APIs for managing chicken flocks")
public class FlockController {

        @Autowired
        private FlockService flockService;

        @Autowired
        private DailyLogService dailyLogService;

        @GetMapping
        @Operation(summary = "Get all flocks")
        public ResponseEntity<ApiResponse<List<FlockDTO>>> getAllFlocks() {
                List<Flock> flocks = flockService.getAllFlocks();

                List<FlockDTO> flockDTOs = flocks.stream()
                                .map(flock -> FlockDTO.builder()
                                                .id(flock.getId())
                                                .name(flock.getName())
                                                .batchCode(flock.getBatchCode())
                                                .coopName(flock.getCoop() != null ? flock.getCoop().getName() : null)
                                                .breedName(flock.getBreed() != null ? flock.getBreed().getName() : null)
                                                .initialQuantity(flock.getInitialQuantity())
                                                .currentQuantity(flock.getCurrentQuantity())
                                                .status(flock.getStatus())
                                                .importDate(flock.getImportDate())
                                                .build())
                                .collect(Collectors.toList());

                ApiResponse<List<FlockDTO>> response = ApiResponse.<List<FlockDTO>>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Flock list retrieved successfully")
                                .data(flockDTOs)
                                .build();
                return ResponseEntity.ok(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get flock by id")
        public ResponseEntity<ApiResponse<FlockDTO>> getFlockById(@PathVariable Long id) {
                Flock flock = flockService.getFlockById(id);

                FlockDTO flockDTO = FlockDTO.builder()
                                .id(flock.getId())
                                .name(flock.getName())
                                .batchCode(flock.getBatchCode())
                                .coopName(flock.getCoop() != null ? flock.getCoop().getName() : null)
                                .breedName(flock.getBreed() != null ? flock.getBreed().getName() : null)
                                .currentQuantity(flock.getCurrentQuantity())
                                .status(flock.getStatus())
                                .importDate(flock.getImportDate())
                                .build();

                ApiResponse<FlockDTO> response = ApiResponse.<FlockDTO>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Flock retrieved successfully")
                                .data(flockDTO)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/import")
        @Operation(summary = "Import a new flock")
        public ResponseEntity<ApiResponse<Void>> importFlock(@Valid @RequestBody FlockImportDTO flockImportDTO) {
                flockService.importFlock(flockImportDTO);
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Đàn gà đã được nhập thành công và lịch trình đã được tạo tự động.")
                                .build();
                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @PostMapping("/sell")
        @Operation(summary = "Sell a flock")
        public ResponseEntity<ApiResponse<Void>> sellFlock(@Valid @RequestBody SellFlockDTO sellFlockDTO) {
                flockService.sellFlock(sellFlockDTO);
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Đàn gà đã được bán thành công.")
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/{id}/close")
        @Operation(summary = "Close a flock")
        public ResponseEntity<ApiResponse<Void>> closeFlock(@PathVariable Long id) {
                flockService.closeFlock(id);
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Đàn gà đã được đóng và chuồng đã được giải phóng.")
                                .build();
                return ResponseEntity.ok(response);
        }

        @GetMapping("/{flockId}/transactions")
        @Operation(summary = "Get all transactions for a flock")
        public ResponseEntity<ApiResponse<FlockTransactionSummaryDTO>> getFlockTransactions(
                        @PathVariable Long flockId) {
                // Lấy danh sách giao dịch
                List<Transaction> transactions = flockService.getTransactionsByFlockId(flockId);

                // Chuyển đổi sang DTO
                List<FlockTransactionDTO> transactionDTOs = transactions.stream()
                                .map(t -> FlockTransactionDTO.builder()
                                                .id(t.getId())
                                                .transactionDate(t.getTransactionDate())
                                                .type(t.getType())
                                                .category(t.getCategory())
                                                .amount(t.getAmount())
                                                .description(t.getDescription())
                                                .createdBy(t.getCreatedBy() != null ? t.getCreatedBy().getFullName()
                                                                : null)
                                                .build())
                                .collect(Collectors.toList());

                // Tính tổng thu, tổng chi
                BigDecimal totalIncome = transactions.stream()
                                .filter(t -> t.getType() == TransactionType.INCOME)
                                .map(t -> t.getAmount())
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalExpense = transactions.stream()
                                .filter(t -> t.getType() == TransactionType.EXPENSE)
                                .map(t -> t.getAmount())
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Tính chi phí vật tư
                BigDecimal materialCost = flockService.calculateMaterialCostForFlock(flockId);

                // Tạo DTO tổng hợp
                FlockTransactionSummaryDTO summaryDTO = FlockTransactionSummaryDTO.builder()
                                .flockId(flockId)
                                .transactions(transactionDTOs)
                                .totalIncome(totalIncome)
                                .totalExpense(totalExpense)
                                .materialCost(materialCost)
                                .totalCost(totalExpense.add(materialCost))
                                .netProfit(totalIncome.subtract(totalExpense.add(materialCost)))
                                .build();

                ApiResponse<FlockTransactionSummaryDTO> response = ApiResponse.<FlockTransactionSummaryDTO>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Danh sách giao dịch đã được lấy thành công.")
                                .data(summaryDTO)
                                .build();
                return ResponseEntity.ok(response);
        }

        @GetMapping("/{flockId}/profit")
        @Operation(summary = "Calculate profit for a flock")
        public ResponseEntity<ApiResponse<FlockProfitDTO>> calculateFlockProfit(@PathVariable Long flockId) {
                Flock flock = flockService.getFlockById(flockId);

                // Tính tổng thu nhập từ transactions
                BigDecimal totalIncome = flockService.getTransactionsByFlockId(flockId).stream()
                                .filter(t -> t.getType() == TransactionType.INCOME)
                                .map(t -> t.getAmount())
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Tính tổng chi phí từ transactions
                BigDecimal totalExpense = flockService.getTransactionsByFlockId(flockId).stream()
                                .filter(t -> t.getType() == TransactionType.EXPENSE)
                                .map(t -> t.getAmount())
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Tính chi phí vật tư
                BigDecimal feedAndMedicineCost = flockService.calculateMaterialCostForFlock(flockId);

                // Tổng chi phí = chi phí giao dịch + chi phí vật tư
                BigDecimal totalCost = totalExpense.add(feedAndMedicineCost);

                // Lợi nhuận
                BigDecimal profit = totalIncome.subtract(totalCost);

                FlockProfitDTO profitDTO = FlockProfitDTO.builder()
                                .flockId(flockId)
                                .flockName(flock.getBatchCode() + " - " + flock.getName())
                                .totalIncome(totalIncome)
                                .totalExpense(totalExpense)
                                .feedAndMedicineCost(feedAndMedicineCost)
                                .totalCost(totalCost)
                                .profit(profit)
                                .build();

                ApiResponse<FlockProfitDTO> response = ApiResponse.<FlockProfitDTO>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Lợi nhuận đã được tính toán thành công.")
                                .data(profitDTO)
                                .build();
                return ResponseEntity.ok(response);
        }

        @GetMapping("/{flockId}/daily-logs")
        @Operation(summary = "Get daily logs for a flock")
        public ResponseEntity<ApiResponse<List<DailyLogResponseDTO>>> getDailyLogsByFlock(@PathVariable Long flockId) {
                // Giả sử có service để lấy daily logs
                List<DailyLogResponseDTO> logs = dailyLogService.getDailyLogsByFlockId(flockId);

                ApiResponse<List<DailyLogResponseDTO>> response = ApiResponse.<List<DailyLogResponseDTO>>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Nhật ký nuôi đã được lấy thành công.")
                                .data(logs)
                                .build();
                return ResponseEntity.ok(response);
        }
// Xóa
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteFlock(@PathVariable Long id) {
                flockService.deleteFlock(id);

                ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Xoá đàn thành công")
                .build();

                return ResponseEntity.ok(response);
        }
//Update
        @PutMapping("/{id}")
        @Operation(summary = "Update flock")
        public ResponseEntity<ApiResponse<FlockDTO>> updateFlock(
                @PathVariable Long id,
                @RequestBody UpdateFlockDTO dto
        ) {
                FlockDTO updatedFlock = flockService.updateFlock(id, dto);

                ApiResponse<FlockDTO> response = ApiResponse.<FlockDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Cập nhật đàn thành công")
                        .data(updatedFlock)
                        .build();

                return ResponseEntity.ok(response);
        }


}