package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.WorkFlowStageAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowStageActionRepository extends JpaRepository<WorkFlowStageAction, UUID> {
    List<WorkFlowStageAction> findByStageId(UUID stageId);

    Optional<WorkFlowStageAction> findByTransitionId(UUID transitionId);
}
