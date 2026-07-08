package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.entities.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, UUID> {
    List<Checklist> findAllByOrderByNameAsc();
}
