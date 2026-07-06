package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.WorkFlowMappingRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowMappingResponse;
import com.example.sacco_core_banking.entities.WorkFlow;
import com.example.sacco_core_banking.entities.WorkFlowMapping;
import com.example.sacco_core_banking.repositories.WorkFlowMappingRepository;
import com.example.sacco_core_banking.repositories.WorkFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin CRUD for the plug-in registry: which processTypeKey (a calling module's own
 * identifier, e.g. "CLAIM") resolves to which WorkFlow. WorkFlowEngineService reads this
 * to start new instances.
 */
@Service
@Transactional
public class WorkFlowMappingService {

    @Autowired
    private WorkFlowMappingRepository workFlowMappingRepository;
    @Autowired
    private WorkFlowRepository workFlowRepository;

    public List<WorkFlowMappingResponse> listMappings() {
        return workFlowMappingRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkFlowMappingResponse createMapping(WorkFlowMappingRequest request) {
        workFlowMappingRepository.findByProcessTypeKey(request.getProcessTypeKey()).ifPresent(existing -> {
            throw new DuplicateResourceException("A mapping for this process type already exists");
        });

        WorkFlow workFlow = workFlowRepository.findById(request.getWorkFlowId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkFlow not found"));

        WorkFlowMapping mapping = new WorkFlowMapping();
        mapping.setProcessTypeKey(request.getProcessTypeKey());
        mapping.setWorkFlow(workFlow);
        mapping.setActive(request.isActive());

        return toResponse(workFlowMappingRepository.save(mapping));
    }

    public WorkFlowMappingResponse updateMapping(UUID id, WorkFlowMappingRequest request) {
        WorkFlowMapping mapping = workFlowMappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));

        workFlowMappingRepository.findByProcessTypeKey(request.getProcessTypeKey())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("A mapping for this process type already exists");
                });

        WorkFlow workFlow = workFlowRepository.findById(request.getWorkFlowId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkFlow not found"));

        mapping.setProcessTypeKey(request.getProcessTypeKey());
        mapping.setWorkFlow(workFlow);
        mapping.setActive(request.isActive());

        return toResponse(workFlowMappingRepository.save(mapping));
    }

    public void deleteMapping(UUID id) {
        WorkFlowMapping mapping = workFlowMappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));
        workFlowMappingRepository.delete(mapping);
    }

    private WorkFlowMappingResponse toResponse(WorkFlowMapping mapping) {
        return WorkFlowMappingResponse.builder()
                .id(mapping.getId())
                .processTypeKey(mapping.getProcessTypeKey())
                .workFlowId(mapping.getWorkFlow().getId())
                .workFlowName(mapping.getWorkFlow().getName())
                .active(mapping.isActive())
                .build();
    }
}
