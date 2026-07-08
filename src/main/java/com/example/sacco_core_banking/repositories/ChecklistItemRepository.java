package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.entities.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, UUID> {
    List<ChecklistItem> findByChecklistIdOrderByOrderNoAsc(UUID checklistId);

    long countByChecklistId(UUID checklistId);

    long countByChecklistIdAndRequiredTrue(UUID checklistId);

    long countByRequiredTrue();

    void deleteByChecklistId(UUID checklistId);
}
