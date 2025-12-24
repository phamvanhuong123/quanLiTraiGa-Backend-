package com.example.springbootchickentmanagerment.repository;

import com.example.springbootchickentmanagerment.entity.User;
import com.example.springbootchickentmanagerment.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user); // Add this method
}
