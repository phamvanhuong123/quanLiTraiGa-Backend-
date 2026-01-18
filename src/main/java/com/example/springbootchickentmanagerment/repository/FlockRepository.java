package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Flock;
import com.example.springbootchickentmanagerment.enums.FlockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlockRepository extends JpaRepository<Flock, Long> {
    @Query("SELECT SUM(f.currentQuantity) FROM Flock f WHERE f.status = :status")
    Long sumCurrentQuantityByStatus(FlockStatus status);

    boolean existsFlockBySupplierId(Long id);
    boolean existsFlockByBreedId(Long id);
    boolean existsFlockByCoopId(Long id);

    @Query("SELECT f FROM Flock f WHERE f.coop.id = :coopId")
    List<Flock> listFlockByCoopId(@Param("coopId") Long coopId);

    @Query("""
    SELECT MIN(f.importDate)
    FROM Flock f
    WHERE f.coop.id = :coopId
    """)
    LocalDate findEarliestStartDateByCoopId(@Param("coopId") Long coopId);

    List<Flock> findAllByCoopId(Long coopId);
}
