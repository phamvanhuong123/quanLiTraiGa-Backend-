package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Transaction;
import com.example.springbootchickentmanagerment.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumAmountByType(TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.flock.id = :flockId ORDER BY t.transactionDate DESC")
    List<Transaction> findByFlockIdOrderByTransactionDateDesc(@Param("flockId") Long flockId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.flock.id = :flockId AND t.type = 'INCOME'")
    BigDecimal sumIncomeByFlockId(@Param("flockId") Long flockId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.flock.id = :flockId AND t.type = 'EXPENSE'")
    BigDecimal sumExpenseByFlockId(@Param("flockId") Long flockId);

    @Query("SELECT t FROM Transaction t WHERE t.flock.id = :flockId AND t.type = :type ORDER BY t.transactionDate DESC")
    List<Transaction> findByFlockIdAndType(@Param("flockId") Long flockId, @Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.flock.id = :flockId ORDER BY t.transactionDate DESC")
    List<Transaction> findByFlockId(@Param("flockId") Long flockId);
}