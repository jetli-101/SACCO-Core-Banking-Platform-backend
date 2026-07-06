package com.example.sacco_core_banking.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.entities.WorkFlowInstance;
import com.example.sacco_core_banking.enums.WorkFlowInstanceStatus;
import com.example.sacco_core_banking.enums.WorkFlowPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowInstanceRepository extends JpaRepository<WorkFlowInstance, UUID> {
    List<WorkFlowInstance> findByStatusIn(List<WorkFlowInstanceStatus> statuses);

    long countByStatusIn(List<WorkFlowInstanceStatus> statuses);

    long countByPriorityAndStatusIn(WorkFlowPriority priority, List<WorkFlowInstanceStatus> statuses);

    long countByStatus(WorkFlowInstanceStatus status);

    long countByDueDateBetweenAndStatusIn(OffsetDateTime from, OffsetDateTime to, List<WorkFlowInstanceStatus> statuses);
}
