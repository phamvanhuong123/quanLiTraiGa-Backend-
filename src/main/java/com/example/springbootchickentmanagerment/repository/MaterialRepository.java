package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Material;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findAll(Sort sort);
}
