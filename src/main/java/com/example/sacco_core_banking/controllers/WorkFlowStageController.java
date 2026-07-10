package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageResponse;
import com.example.sacco_core_banking.services.WorkFlowStageService;
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
 * CRUD for a workflow's stages — powers the create wizard's Stage Structure step and the
 * Edit Workflow "Stages" tab. No blanket role restriction here: WorkFlowStageService checks
 * access to the stage's parent workflow on every call (ROLE_SYSTEM_ADMINISTRATOR always
 * passes; anyone else needs that specific workflow's allowedRoles).
 */
@RestController
@RequestMapping(path = Constants.WORKFLOW_STAGES_PATH)
@Tag(name = "Workflow Stage Management", description = "APIs for managing workflow stages")
public class WorkFlowStageController {

    @Autowired
    private WorkFlowStageService workFlowStageService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List stages", description = "Lists stages, optionally filtered to one workflow.")
    public ResponseEntity<ApiResponse<List<WorkFlowStageResponse>>> listStages(
            @Parameter(description = "Workflow to list stages for", required = true)
            @RequestParam UUID workFlowId) {
        return ResponseEntity.ok(ApiResponse.success(workFlowStageService.listStages(workFlowId, currentUser.get()), "success"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a stage by id")
    public ResponseEntity<ApiResponse<WorkFlowStageResponse>> getStage(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(workFlowStageService.getStageById(id, currentUser.get()), "success"));
    }

    @PostMapping
    @Operation(summary = "Create a stage")
    public ResponseEntity<ApiResponse<WorkFlowStageResponse>> createStage(@Valid @RequestBody WorkFlowStageRequest request) {
        WorkFlowStageResponse created = workFlowStageService.createStage(request, currentUser.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Stage created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a stage")
    public ResponseEntity<ApiResponse<WorkFlowStageResponse>> updateStage(
            @PathVariable UUID id, @Valid @RequestBody WorkFlowStageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(workFlowStageService.updateStage(id, request, currentUser.get()), "success"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a stage")
    public ResponseEntity<ApiResponse<Void>> deleteStage(@PathVariable UUID id) {
        workFlowStageService.deleteStage(id, currentUser.get());
        return ResponseEntity.ok(ApiResponse.success(null, "Stage deleted successfully"));
    }
}
