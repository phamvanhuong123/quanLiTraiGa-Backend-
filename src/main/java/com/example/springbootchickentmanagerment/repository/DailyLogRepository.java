package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    
    @Query("SELECT dl FROM DailyLog dl WHERE dl.flock.id = :flockId ORDER BY dl.logDate DESC")
    List<DailyLog> findByFlockId(@Param("flockId") Long flockId);
    
    @Query("SELECT dl FROM DailyLog dl WHERE dl.flock.id = :flockId AND dl.logDate = :logDate")
    List<DailyLog> findByFlockIdAndLogDate(@Param("flockId") Long flockId, @Param("logDate") LocalDate logDate);
    
    boolean existsByFlockIdAndLogDate(Long flockId, LocalDate logDate);
}