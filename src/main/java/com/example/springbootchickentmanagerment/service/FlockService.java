package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.flock.FlockImportDTO;
import com.example.springbootchickentmanagerment.dto.flock.SellFlockDTO;
import com.example.springbootchickentmanagerment.entity.*;
import com.example.springbootchickentmanagerment.enums.CoopStatus;
import com.example.springbootchickentmanagerment.enums.FlockStatus;
import com.example.springbootchickentmanagerment.enums.ScheduleStatus;
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
import com.example.springbootchickentmanagerment.dto.flock.FlockDTO;
import com.example.springbootchickentmanagerment.dto.flock.UpdateFlockDTO;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlockService {

        @Autowired
        private FlockRepository flockRepository;

        @Autowired
        private CoopRepository coopRepository;

        @Autowired
        private BreedRepository breedRepository;

        @Autowired
        private SupplierRepository supplierRepository;

        @Autowired
        private TransactionRepository transactionRepository;

        @Autowired
        private ScheduleRepository scheduleRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private DailyLogRepository dailyLogRepository;

        @Autowired
        private DailyLogDetailRepository dailyLogDetailRepository;

        public List<Flock> getAllFlocks() {
                return flockRepository.findAll();
        }

        @Transactional
        public void importFlock(FlockImportDTO dto) {
                User currentUser = getCurrentUser();
                LocalDate importDate = LocalDate.now();

                // 1. Kiểm tra chuồng
                Coop coop = coopRepository.findById(dto.getCoopId())
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Chuồng không tồn tại"));

                if (coop.getStatus() != CoopStatus.EMPTY) {
                        throw new CustomException(HttpStatus.BAD_REQUEST, "Chuồng đang có gà hoặc chưa được dọn dẹp");
                }

                if (dto.getQuantity() > coop.getCapacity()) {
                        throw new CustomException(HttpStatus.BAD_REQUEST,
                                        String.format("Số lượng gà (%d) vượt quá sức chứa của chuồng (%d)",
                                                        dto.getQuantity(), coop.getCapacity()));
                }

                // 2. Kiểm tra giống
                Breed breed = breedRepository.findById(dto.getBreedId())
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Giống gà không tồn tại"));

                // 3. Kiểm tra nhà cung cấp
                Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                                "Nhà cung cấp không tồn tại"));

                // 4. Tạo đàn gà
                Flock flock = Flock.builder()
                                .name(dto.getName())
                                .breed(breed)
                                .coop(coop)
                                .supplier(supplier)
                                .batchCode(generateBatchCode())
                                .importDate(importDate)
                                .initialQuantity(dto.getQuantity())
                                .currentQuantity(dto.getQuantity())
                                .status(FlockStatus.RAISING)
                                .createdBy(currentUser)
                                .build();

                Flock savedFlock = flockRepository.save(flock);

                // 5. Cập nhật trạng thái chuồng
                coop.setStatus(CoopStatus.ACTIVE);
                coopRepository.save(coop);

                // 6. Tạo giao dịch chi phí mua giống
                BigDecimal totalCost = dto.getPricePerChick().multiply(BigDecimal.valueOf(dto.getQuantity()));
                Transaction transaction = Transaction.builder()
                                .transactionDate(importDate)
                                .type(TransactionType.EXPENSE)
                                .category("Mua con giống")
                                .amount(totalCost)
                                .description(String.format("Mua %d con giống %s từ %s",
                                                dto.getQuantity(), breed.getName(), supplier.getName()))
                                .flock(savedFlock)
                                .createdBy(currentUser)
                                .build();
                transactionRepository.save(transaction);

                // 7. Tự động tạo lịch trình
                createInitialSchedules(savedFlock, importDate);

        }

        @Transactional
        public void sellFlock(SellFlockDTO dto) {
                User currentUser = getCurrentUser();

                Flock flock = flockRepository.findById(dto.getFlockId())
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Đàn gà không tồn tại"));

                if (flock.getStatus() != FlockStatus.RAISING) {
                        throw new CustomException(HttpStatus.BAD_REQUEST, "Đàn gà không ở trạng thái đang nuôi");
                }

                if (dto.getSoldQuantity() > flock.getCurrentQuantity()) {
                        throw new CustomException(HttpStatus.BAD_REQUEST,
                                        String.format("Số lượng bán (%d) vượt quá số lượng hiện tại (%d)",
                                                        dto.getSoldQuantity(), flock.getCurrentQuantity()));
                }

                // 1. Cập nhật số lượng đàn
                int newQuantity = flock.getCurrentQuantity() - dto.getSoldQuantity();
                flock.setCurrentQuantity(newQuantity);

                // Nếu bán hết thì đóng đàn
                if (newQuantity == 0 || dto.isCloseFlock()) {
                        flock.setStatus(FlockStatus.SOLD);
                }

                flockRepository.save(flock);

                // 2. Tạo giao dịch thu nhập
                LocalDate transactionDate = dto.getTransactionDate() != null ? dto.getTransactionDate()
                                : LocalDate.now();

                Transaction transaction = Transaction.builder()
                                .transactionDate(transactionDate)
                                .type(TransactionType.INCOME)
                                .category("Bán gà")
                                .amount(dto.getTotalPrice())
                                .description(String.format("Bán %d con gà, tổng trọng lượng %.2f kg",
                                                dto.getSoldQuantity(), dto.getTotalWeight()))
                                .flock(flock)
                                .createdBy(currentUser)
                                .build();
                transactionRepository.save(transaction);

                // 3. Nếu đóng đàn, giải phóng chuồng
                if (dto.isCloseFlock() || newQuantity == 0) {
                        closeFlock(flock.getId());
                }
        }

        @Transactional
        public void closeFlock(Long flockId) {
                Flock flock = flockRepository.findById(flockId)
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Đàn gà không tồn tại"));

                // 1. Cập nhật trạng thái đàn
                flock.setStatus(FlockStatus.SOLD);
                flockRepository.save(flock);

                // 2. Giải phóng chuồng
                Coop coop = flock.getCoop();
                coop.setStatus(CoopStatus.EMPTY);
                coopRepository.save(coop);
        }

        public List<Transaction> getTransactionsByFlockId(Long flockId) {
                return transactionRepository.findByFlockIdOrderByTransactionDateDesc(flockId);
        }

        public Flock getFlockById(Long flockId) {
                return flockRepository.findById(flockId)
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Đàn gà không tồn tại"));
        }

        public BigDecimal calculateFlockProfit(Long flockId) {
                Flock flock = getFlockById(flockId);

                // 1. Tính tổng thu nhập
                BigDecimal totalIncome = transactionRepository.sumIncomeByFlockId(flockId);
                if (totalIncome == null)
                        totalIncome = BigDecimal.ZERO;

                // 2. Tính tổng chi phí từ transactions
                BigDecimal totalExpense = transactionRepository.sumExpenseByFlockId(flockId);
                if (totalExpense == null)
                        totalExpense = BigDecimal.ZERO;

                // 3. Tính chi phí vật tư từ daily logs
                BigDecimal materialCost = calculateMaterialCostForFlock(flockId);

                // 4. Tổng chi phí = chi phí giao dịch + chi phí vật tư
                BigDecimal totalCost = totalExpense.add(materialCost);

                // 5. Lợi nhuận = thu nhập - tổng chi phí
                return totalIncome.subtract(totalCost);
        }

        public BigDecimal calculateMaterialCostForFlock(Long flockId) {
                // Lấy tất cả daily logs của đàn
                List<DailyLog> logs = dailyLogRepository.findByFlockId(flockId);

                BigDecimal totalMaterialCost = BigDecimal.ZERO;

                for (DailyLog log : logs) {
                        // Lấy tất cả chi tiết vật tư của mỗi log
                        List<DailyLogDetail> details = log.getDetails();
                        for (DailyLogDetail detail : details) {
                                BigDecimal quantity = BigDecimal.valueOf(detail.getQuantityUsed());
                                BigDecimal pricePerUnit = detail.getInventoryBatch().getPricePerUnit();
                                BigDecimal cost = quantity.multiply(pricePerUnit);
                                totalMaterialCost = totalMaterialCost.add(cost);
                        }
                }

                return totalMaterialCost;
        }

        private void createInitialSchedules(Flock flock, LocalDate importDate) {
                List<Schedule> schedules = new ArrayList<>();

                // Lịch tiêm phòng cơ bản
                schedules.add(Schedule.builder()
                                .flock(flock)
                                .title("Tiêm Vaccine Marek")
                                .scheduledDate(importDate.plusDays(3))
                                .status(ScheduleStatus.PENDING)
                                .build());

                schedules.add(Schedule.builder()
                                .flock(flock)
                                .title("Tiêm Vaccine Gumboro lần 1")
                                .scheduledDate(importDate.plusDays(7))
                                .status(ScheduleStatus.PENDING)
                                .build());

                schedules.add(Schedule.builder()
                                .flock(flock)
                                .title("Tiêm Vaccine Newcastle")
                                .scheduledDate(importDate.plusDays(14))
                                .status(ScheduleStatus.PENDING)
                                .build());

                schedules.add(Schedule.builder()
                                .flock(flock)
                                .title("Cắt mỏ")
                                .scheduledDate(importDate.plusDays(21))
                                .status(ScheduleStatus.PENDING)
                                .build());

                scheduleRepository.saveAll(schedules);
        }

        private String generateBatchCode() {
                String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                String random = String.valueOf((int) (Math.random() * 1000));
                return "FLOCK-" + timestamp + "-" + random;
        }

        private User getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new CustomException(HttpStatus.UNAUTHORIZED, "User not authenticated");
                }

                String subject = authentication.getName();
                System.out.println("Authentication subject in FlockService: " + subject);

                // Thử tìm bằng email trước
                User currentUser = userRepository.findByEmail(subject)
                                .orElseGet(() -> {
                                        // Nếu không tìm thấy bằng email, thử tìm bằng username
                                        return userRepository.findByUsername(subject)
                                                        .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED,
                                                                        "Người dùng không tồn tại"));
                                });

                return currentUser;
        }
        @Transactional
        public void deleteFlock(Long id) {
                Flock flock = flockRepository.findById(id)
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy đàn"));

                if (flock.getStatus() != FlockStatus.RAISING) {
                        throw new CustomException(HttpStatus.BAD_REQUEST, "Chỉ được xoá đàn đang nuôi");
                }

                Coop coop = flock.getCoop();
                coop.setStatus(CoopStatus.EMPTY);
                coopRepository.save(coop);

                flockRepository.delete(flock);
        }
        @Transactional
        public FlockDTO updateFlock(Long id, UpdateFlockDTO dto) {
                Flock flock = flockRepository.findById(id)
                        .orElseThrow(() ->
                                new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy đàn")
                        );

                // Chỉ cho sửa đàn đang nuôi
                if (flock.getStatus() != FlockStatus.RAISING) {
                        throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "Chỉ được sửa đàn đang nuôi"
                        );
                }

                flock.setName(dto.getName());
                flock.setNotes(dto.getNotes());

                flockRepository.save(flock);

                return FlockDTO.builder()
                        .id(flock.getId())
                        .name(flock.getName())
                        .batchCode(flock.getBatchCode())
                        .coopName(flock.getCoop() != null ? flock.getCoop().getName() : null)
                        .breedName(flock.getBreed() != null ? flock.getBreed().getName() : null)
                        .currentQuantity(flock.getCurrentQuantity())
                        .status(flock.getStatus())
                        .importDate(flock.getImportDate())
                        .notes(flock.getNotes())
                        .build();
        }



}