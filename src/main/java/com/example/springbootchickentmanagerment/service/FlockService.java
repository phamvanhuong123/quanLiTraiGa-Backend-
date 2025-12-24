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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    @Transactional
    public void importFlock(FlockImportDTO dto) {
        User currentUser = getCurrentUser();
        Coop coop = coopRepository.findById(dto.getCoopId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Coop not found"));
        if (coop.getStatus() != CoopStatus.EMPTY) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Chuồng đang có gà hoặc chưa được dọn dẹp");
        }
        Breed breed = breedRepository.findById(dto.getBreedId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Breed not found"));
        Supplier supplier = supplierRepository.findById(dto.getSupplierId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Supplier not found"));
        Flock flock = Flock.builder()
                .breed(breed)
                .coop(coop)
                .supplier(supplier)
                .batchCode("FLOCK-" + dto.getImportDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .importDate(dto.getImportDate())
                .initialQuantity(dto.getQuantity())
                .currentQuantity(dto.getQuantity())
                .status(FlockStatus.RAISING)
                .createdBy(currentUser)
                .build();
        Flock savedFlock = flockRepository.save(flock);

        coop.setStatus(CoopStatus.ACTIVE);
        coopRepository.save(coop);

        BigDecimal totalCost = dto.getPricePerChick().multiply(BigDecimal.valueOf(dto.getQuantity()));
        Transaction transaction = Transaction.builder()
                .transactionDate(dto.getImportDate())
                .type(TransactionType.EXPENSE)
                .category("Mua con giống")
                .amount(totalCost)
                .description("Mua " + dto.getQuantity() + " con giống " + breed.getName() + " từ " + supplier.getName())
                .flock(savedFlock)
                .createdBy(currentUser)
                .build();
        transactionRepository.save(transaction);

        createSchedule(savedFlock, "Tiêm Marek", dto.getImportDate().plusDays(3));
        createSchedule(savedFlock, "Gumboro lần 1", dto.getImportDate().plusDays(7));
    }

    @Transactional
    public void sellFlock(SellFlockDTO dto) {
        User currentUser = getCurrentUser();
        Flock flock = flockRepository.findById(dto.getFlockId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Flock not found"));

        int newQuantity = flock.getCurrentQuantity() - dto.getSoldQuantity();
        if (newQuantity < 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Sold quantity cannot exceed current flock quantity.");
        }
        flock.setCurrentQuantity(newQuantity);
        flockRepository.save(flock);

        Transaction transaction = Transaction.builder()
                .transactionDate(LocalDate.now())
                .type(TransactionType.INCOME)
                .category("Bán gà")
                .amount(dto.getTotalPrice())
                .description("Bán " + dto.getSoldQuantity() + " con gà từ đàn " + flock.getBatchCode())
                .flock(flock)
                .createdBy(currentUser)
                .build();
        transactionRepository.save(transaction);
    }

    @Transactional
    public void closeFlock(Long flockId) {
        Flock flock = flockRepository.findById(flockId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Flock not found"));

        flock.setStatus(FlockStatus.SOLD);
        flockRepository.save(flock);

        Coop coop = flock.getCoop();
        coop.setStatus(CoopStatus.EMPTY);
        coopRepository.save(coop);
    }

    private void createSchedule(Flock flock, String title, java.time.LocalDate date) {
        Schedule schedule = Schedule.builder()
                .flock(flock)
                .title(title)
                .scheduledDate(date)
                .status(ScheduleStatus.PENDING)
                .build();
        scheduleRepository.save(schedule);
    }

    private User getCurrentUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
