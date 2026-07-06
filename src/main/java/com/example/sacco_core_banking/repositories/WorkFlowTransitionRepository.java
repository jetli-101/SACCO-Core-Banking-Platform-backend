package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.entities.WorkFlowTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowTransitionRepository extends JpaRepository<WorkFlowTransition, UUID> {
    List<WorkFlowTransition> findByWorkFlowId(UUID workFlowId);

    List<WorkFlowTransition> findByWorkFlowIdAndFromStageIdAndActiveTrue(UUID workFlowId, UUID fromStageId);
}
