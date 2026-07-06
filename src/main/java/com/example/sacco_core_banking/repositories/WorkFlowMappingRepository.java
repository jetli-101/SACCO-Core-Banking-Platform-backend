package com.example.sacco_core_banking.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.WorkFlowMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowMappingRepository extends JpaRepository<WorkFlowMapping, UUID> {
    Optional<WorkFlowMapping> findByProcessTypeKeyAndActiveTrue(String processTypeKey);

    Optional<WorkFlowMapping> findByProcessTypeKey(String processTypeKey);
}
