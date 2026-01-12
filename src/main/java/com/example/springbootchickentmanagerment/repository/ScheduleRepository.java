package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Schedule;
import com.example.springbootchickentmanagerment.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByFlockId(Long flockId);
    
    @Query("SELECT s FROM Schedule s WHERE s.scheduledDate <= :date AND s.status = 'PENDING'")
    List<Schedule> findPendingSchedulesUpToDate(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Schedule s WHERE s.scheduledDate BETWEEN :startDate AND :endDate")
    List<Schedule> findSchedulesBetweenDates(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.scheduledDate <= :date AND s.status = 'PENDING'")
    long countPendingSchedulesUpToDate(@Param("date") LocalDate date);
    
    List<Schedule> findByStatus(ScheduleStatus status);
}