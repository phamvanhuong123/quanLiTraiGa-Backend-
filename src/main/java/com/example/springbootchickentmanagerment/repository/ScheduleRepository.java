package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByFlockId(Long flockId);
}
