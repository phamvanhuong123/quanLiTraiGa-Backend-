package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Coop;
import com.example.springbootchickentmanagerment.enums.CoopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoopRepository extends JpaRepository<Coop, Long> {
    List<Coop> findByStatus(CoopStatus status);
}