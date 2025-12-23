package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Coop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoopRepository extends JpaRepository<Coop, Long> {
}
