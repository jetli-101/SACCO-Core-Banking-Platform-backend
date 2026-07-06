package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStatusRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStatusResponse;
import com.example.sacco_core_banking.entities.WorkFlowStatus;
import com.example.sacco_core_banking.repositories.WorkFlowStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD for the shared WorkFlowStatus catalog referenced by transitions/stages. */
@Service
@Transactional
public class WorkFlowStatusService {

    @Autowired
    private WorkFlowStatusRepository workFlowStatusRepository;

    public List<WorkFlowStatusResponse> listStatuses() {
        return workFlowStatusRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkFlowStatusResponse createStatus(WorkFlowStatusRequest request) {
        workFlowStatusRepository.findByTextId(request.getTextId()).ifPresent(existing -> {
            throw new DuplicateResourceException("A status with this text ID already exists");
        });

        WorkFlowStatus status = new WorkFlowStatus();
        status.setName(request.getName());
        status.setTextId(request.getTextId());
        status.setDescription(request.getDescription());

        return toResponse(workFlowStatusRepository.save(status));
    }

    public WorkFlowStatusResponse updateStatus(UUID id, WorkFlowStatusRequest request) {
        WorkFlowStatus status = workFlowStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        workFlowStatusRepository.findByTextId(request.getTextId())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("A status with this text ID already exists");
                });

        status.setName(request.getName());
        status.setTextId(request.getTextId());
        status.setDescription(request.getDescription());

        return toResponse(workFlowStatusRepository.save(status));
    }

    public void deleteStatus(UUID id) {
        WorkFlowStatus status = workFlowStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        workFlowStatusRepository.delete(status);
    }

    private WorkFlowStatusResponse toResponse(WorkFlowStatus status) {
        return WorkFlowStatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .textId(status.getTextId())
                .description(status.getDescription())
                .build();
    }
}
