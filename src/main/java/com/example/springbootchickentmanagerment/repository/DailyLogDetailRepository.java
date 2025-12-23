package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.DailyLogDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyLogDetailRepository extends JpaRepository<DailyLogDetail, Long> {
}
