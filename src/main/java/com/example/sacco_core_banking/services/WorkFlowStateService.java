package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStateRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStateResponse;
import com.example.sacco_core_banking.entities.WorkFlowState;
import com.example.sacco_core_banking.repositories.WorkFlowStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD for the shared WorkFlowState catalog (DRAFT/ACTIVE/ARCHIVED, seeded by WorkFlowStateSeeder). */
@Service
@Transactional
public class WorkFlowStateService {

    @Autowired
    private WorkFlowStateRepository workFlowStateRepository;

    public List<WorkFlowStateResponse> listStates() {
        return workFlowStateRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkFlowStateResponse createState(WorkFlowStateRequest request) {
        workFlowStateRepository.findByTextId(request.getTextId()).ifPresent(existing -> {
            throw new DuplicateResourceException("A state with this text ID already exists");
        });

        WorkFlowState state = new WorkFlowState();
        state.setName(request.getName());
        state.setTextId(request.getTextId());
        state.setDescription(request.getDescription());

        return toResponse(workFlowStateRepository.save(state));
    }

    public WorkFlowStateResponse updateState(UUID id, WorkFlowStateRequest request) {
        WorkFlowState state = workFlowStateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found"));

        workFlowStateRepository.findByTextId(request.getTextId())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("A state with this text ID already exists");
                });

        state.setName(request.getName());
        state.setTextId(request.getTextId());
        state.setDescription(request.getDescription());

        return toResponse(workFlowStateRepository.save(state));
    }

    public void deleteState(UUID id) {
        WorkFlowState state = workFlowStateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found"));
        workFlowStateRepository.delete(state);
    }

    private WorkFlowStateResponse toResponse(WorkFlowState state) {
        return WorkFlowStateResponse.builder()
                .id(state.getId())
                .name(state.getName())
                .textId(state.getTextId())
                .description(state.getDescription())
                .build();
    }
}
