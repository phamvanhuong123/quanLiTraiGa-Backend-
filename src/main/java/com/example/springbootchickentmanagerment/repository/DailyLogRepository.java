package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
}
