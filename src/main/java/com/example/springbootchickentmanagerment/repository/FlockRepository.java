package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Flock;
import com.example.springbootchickentmanagerment.enums.FlockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlockRepository extends JpaRepository<Flock, Long> {
    List<Flock> findByCoopId(Long coopId);
    List<Flock> findByStatus(FlockStatus status);
    List<Flock> findByCoopIdAndStatus(Long coopId, FlockStatus status);
}
