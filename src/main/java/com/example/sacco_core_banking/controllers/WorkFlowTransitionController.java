package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowTransitionRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowTransitionResponse;
import com.example.sacco_core_banking.services.WorkFlowTransitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD for a workflow's transitions — powers the create wizard's Connect Components step and
 * the Edit Workflow "Transitions" tab. No blanket role restriction here: WorkFlowTransitionService
 * checks access to the transition's parent workflow on every call.
 */
@RestController
@RequestMapping(path = Constants.WORKFLOW_TRANSITIONS_PATH)
@Tag(name = "Workflow Transition Management", description = "APIs for managing workflow transitions")
public class WorkFlowTransitionController {

    @Autowired
    private WorkFlowTransitionService workFlowTransitionService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List transitions", description = "Lists transitions for one workflow.")
    public ResponseEntity<ApiResponse<List<WorkFlowTransitionResponse>>> listTransitions(
            @Parameter(description = "Workflow to list transitions for", required = true)
            @RequestParam UUID workFlowId) {
        return ResponseEntity.ok(ApiResponse.success(workFlowTransitionService.listTransitions(workFlowId, currentUser.get()), "success"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a transition by id")
    public ResponseEntity<ApiResponse<WorkFlowTransitionResponse>> getTransition(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(workFlowTransitionService.getTransitionById(id, currentUser.get()), "success"));
    }

    @PostMapping
    @Operation(summary = "Create a transition")
    public ResponseEntity<ApiResponse<WorkFlowTransitionResponse>> createTransition(@Valid @RequestBody WorkFlowTransitionRequest request) {
        WorkFlowTransitionResponse created = workFlowTransitionService.createTransition(request, currentUser.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Transition created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a transition")
    public ResponseEntity<ApiResponse<WorkFlowTransitionResponse>> updateTransition(
            @PathVariable UUID id, @Valid @RequestBody WorkFlowTransitionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(workFlowTransitionService.updateTransition(id, request, currentUser.get()), "success"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transition")
    public ResponseEntity<ApiResponse<Void>> deleteTransition(@PathVariable UUID id) {
        workFlowTransitionService.deleteTransition(id, currentUser.get());
        return ResponseEntity.ok(ApiResponse.success(null, "Transition deleted successfully"));
    }
}
