package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.ModuleRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRegisterRepository extends JpaRepository<ModuleRegister, UUID> {
    Optional<ModuleRegister> findByTextId(String textId);

    List<ModuleRegister> findByParentIsNullOrderByOrderNo();
}
