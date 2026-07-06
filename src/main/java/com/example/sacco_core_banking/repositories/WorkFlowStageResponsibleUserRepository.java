package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.WorkFlowStageResponsibleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowStageResponsibleUserRepository extends JpaRepository<WorkFlowStageResponsibleUser, UUID> {
    List<WorkFlowStageResponsibleUser> findByStageId(UUID stageId);

    Optional<WorkFlowStageResponsibleUser> findByStageIdAndUserId(UUID stageId, UUID userId);

    void deleteByStageId(UUID stageId);
}
