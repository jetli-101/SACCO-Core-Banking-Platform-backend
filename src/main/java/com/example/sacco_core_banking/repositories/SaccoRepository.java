package com.example.sacco_core_banking.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.Sacco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaccoRepository extends JpaRepository<Sacco, UUID> {
    Optional<Sacco> findByRegistrationNumber(String registrationNumber);
}
