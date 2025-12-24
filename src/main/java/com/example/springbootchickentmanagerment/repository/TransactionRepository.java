package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Transaction;
import com.example.springbootchickentmanagerment.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumAmountByType(TransactionType type);
}
