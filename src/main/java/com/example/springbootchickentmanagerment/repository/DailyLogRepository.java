package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    List<DailyLog> findByFlockIdOrderByLogDateDesc(Long flockId);
    Optional<DailyLog> findByFlockIdAndLogDate(Long flockId, LocalDate logDate);
}
