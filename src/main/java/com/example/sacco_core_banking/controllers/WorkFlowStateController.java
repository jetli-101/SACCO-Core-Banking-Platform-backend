package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStateRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStateResponse;
import com.example.sacco_core_banking.services.WorkFlowStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin CRUD for the shared WorkFlowState catalog (DRAFT/ACTIVE/ARCHIVED). */
@RestController
@RequestMapping(path = Constants.WORKFLOW_STATES_PATH)
@Tag(name = "Workflow State Management", description = "APIs for managing the shared workflow stage-lifecycle state catalog")
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
public class WorkFlowStateController {

    @Autowired
    private WorkFlowStateService workFlowStateService;

    @GetMapping
    @Operation(summary = "List states")
    public ResponseEntity<ApiResponse<List<WorkFlowStateResponse>>> listStates() {
        return ResponseEntity.ok(ApiResponse.success(workFlowStateService.listStates(), "success"));
    }

    @PostMapping
    @Operation(summary = "Create a state")
    public ResponseEntity<ApiResponse<WorkFlowStateResponse>> createState(@Valid @RequestBody WorkFlowStateRequest request) {
        WorkFlowStateResponse created = workFlowStateService.createState(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "State created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a state")
    public ResponseEntity<ApiResponse<WorkFlowStateResponse>> updateState(
            @PathVariable UUID id, @Valid @RequestBody WorkFlowStateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(workFlowStateService.updateState(id, request), "success"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a state")
    public ResponseEntity<ApiResponse<Void>> deleteState(@PathVariable UUID id) {
        workFlowStateService.deleteState(id);
        return ResponseEntity.ok(ApiResponse.success(null, "State deleted successfully"));
    }
}
