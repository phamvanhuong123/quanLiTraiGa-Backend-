package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Flock;
import com.example.springbootchickentmanagerment.enums.FlockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FlockRepository extends JpaRepository<Flock, Long> {
    @Query("SELECT SUM(f.currentQuantity) FROM Flock f WHERE f.status = :status")
    Long sumCurrentQuantityByStatus(FlockStatus status);
}
