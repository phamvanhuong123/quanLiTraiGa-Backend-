package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {
}
