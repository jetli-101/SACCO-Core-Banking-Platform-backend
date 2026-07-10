package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowDashboardResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowResponse;
import com.example.sacco_core_banking.services.WorkFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

/**
 * CRUD for workflow definitions — powers the Workflows list, the create wizard's Core
 * Components step, and the Workflow Details tab. Stages live in WorkFlowStageController,
 * transitions in WorkFlowTransitionController. Only ROLE_SYSTEM_ADMINISTRATOR may create or
 * delete a workflow; viewing/updating an existing one is also open to whichever additional
 * roles that specific workflow has granted access to (WorkFlow.allowedRoles), enforced in
 * WorkFlowService rather than here so the same rule applies uniformly to list/get/update.
 */
@RestController
@RequestMapping(path = Constants.WORKFLOWS_PATH)
@Tag(name = "Workflow Management", description = "APIs for managing workflow definitions")
public class WorkFlowController {

    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List workflows", description = "Returns every workflow definition the caller is authorized to see.")
    public ResponseEntity<ApiResponse<List<WorkFlowResponse>>> listWorkFlows() {
        return ResponseEntity.ok(ApiResponse.success(workFlowService.listWorkFlows(currentUser.get()), "success"));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Workflow dashboard stats", description = "Total/active workflows, active processes, average stages.")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<WorkFlowDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(workFlowService.getDashboard(), "success"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a workflow by id")
    public ResponseEntity<ApiResponse<WorkFlowResponse>> getWorkFlow(
            @Parameter(description = "ID of the workflow", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(workFlowService.getWorkFlowById(id, currentUser.get()), "success"));
    }

    @PostMapping
    @Operation(summary = "Create a workflow", description = "Step 1 of the create wizard (Core Components).")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<WorkFlowResponse>> createWorkFlow(@Valid @RequestBody WorkFlowRequest request) {
        WorkFlowResponse created = workFlowService.createWorkFlow(request, currentUser.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Workflow created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a workflow")
    public ResponseEntity<ApiResponse<WorkFlowResponse>> updateWorkFlow(
            @PathVariable UUID id, @Valid @RequestBody WorkFlowRequest request) {
        return ResponseEntity.ok(ApiResponse.success(workFlowService.updateWorkFlow(id, request, currentUser.get()), "success"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a workflow")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteWorkFlow(@PathVariable UUID id) {
        workFlowService.deleteWorkFlow(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Workflow deleted successfully"));
    }
}
