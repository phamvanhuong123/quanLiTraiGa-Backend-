package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.transaction.TransactionRequest;
import com.example.springbootchickentmanagerment.dto.transaction.TransactionResponse;
import com.example.springbootchickentmanagerment.dto.transaction.TransactionSummaryResponse;
import com.example.springbootchickentmanagerment.entity.Flock;
import com.example.springbootchickentmanagerment.entity.Transaction;
import com.example.springbootchickentmanagerment.entity.User;
import com.example.springbootchickentmanagerment.enums.TransactionType;
import com.example.springbootchickentmanagerment.repository.FlockRepository;
import com.example.springbootchickentmanagerment.repository.TransactionRepository;
import com.example.springbootchickentmanagerment.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final FlockRepository flockRepository;
    private final UserRepository userRepository;

    public TransactionSummaryResponse getFilteredTransactions(LocalDate startDate, LocalDate endDate, TransactionType type, String category) {
        Specification<Transaction> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endDate));
            }
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (category != null && !category.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("category"), "%" + category + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.ASC, "transactionDate").and(Sort.by(Sort.Direction.ASC, "id"));
        List<Transaction> transactions = transactionRepository.findAll(spec, sort);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TransactionResponse> transactionResponses = transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return TransactionSummaryResponse.builder()
                .transactions(transactionResponses)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netProfit(totalIncome.subtract(totalExpense))
                .build();
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User user = userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByUsername(identifier)
                .orElseThrow(() -> new RuntimeException("User not found: " + identifier)));

        Flock flock = null;
        if (request.getFlockId() != null) {
            flock = flockRepository.findById(request.getFlockId()).orElse(null);
        }

        Transaction transaction = Transaction.builder()
                .transactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDate.now())
                .type(request.getType())
                .category(request.getCategory())
                .amount(request.getAmount())
                .description(request.getDescription())
                .flock(flock)
                .createdBy(user)
                .build();

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (request.getFlockId() != null) {
            Flock flock = flockRepository.findById(request.getFlockId()).orElse(null);
            transaction.setFlock(flock);
        } else {
            transaction.setFlock(null);
        }

        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại"));

        // Kiểm tra nếu có đàn liên quan thì không cho xóa
        if (transaction.getFlock() != null) {
            throw new RuntimeException("Không thể xóa giao dịch này vì có liên quan đến đàn gà: " + transaction.getFlock().getName());
        }

        transactionRepository.delete(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionDate(transaction.getTransactionDate())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .flockName(transaction.getFlock() != null ? transaction.getFlock().getName() : null)
                .createdByFullName(transaction.getCreatedBy() != null ? transaction.getCreatedBy().getFullName() : null)
                .build();
    }
}
