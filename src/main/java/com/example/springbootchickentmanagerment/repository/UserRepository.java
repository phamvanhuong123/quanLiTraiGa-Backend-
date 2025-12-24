package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // Add this method
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
