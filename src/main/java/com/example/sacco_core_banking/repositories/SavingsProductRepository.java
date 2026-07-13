package com.example.sacco_core_banking.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.SavingsProduct;
import com.example.sacco_core_banking.enums.SavingsProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsProductRepository extends JpaRepository<SavingsProduct, UUID> {
    Optional<SavingsProduct> findBySavingsProductType(SavingsProductType savingsProductType);
}
