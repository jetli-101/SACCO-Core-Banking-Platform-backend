package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowMappingRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowMappingResponse;
import com.example.sacco_core_banking.services.WorkFlowMappingService;
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

/**
 * Admin CRUD for the plug-in registry — lets a new module attach to the workflow engine by
 * registering its own processTypeKey against an existing Workflow, without adding any new
 * tables or controllers of its own.
 */
@RestController
@RequestMapping(path = Constants.WORKFLOW_MAPPINGS_PATH)
@Tag(name = "Workflow Mapping Management", description = "APIs for registering which workflow a module's process type resolves to")
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
public class WorkFlowMappingController {

    @Autowired
    private WorkFlowMappingService workFlowMappingService;

    @GetMapping
    @Operation(summary = "List mappings")
    public ResponseEntity<ApiResponse<List<WorkFlowMappingResponse>>> listMappings() {
        return ResponseEntity.ok(ApiResponse.success(workFlowMappingService.listMappings(), "success"));
    }

    @PostMapping
    @Operation(summary = "Create a mapping")
    public ResponseEntity<ApiResponse<WorkFlowMappingResponse>> createMapping(@Valid @RequestBody WorkFlowMappingRequest request) {
        WorkFlowMappingResponse created = workFlowMappingService.createMapping(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Mapping created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a mapping")
    public ResponseEntity<ApiResponse<WorkFlowMappingResponse>> updateMapping(
            @PathVariable UUID id, @Valid @RequestBody WorkFlowMappingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(workFlowMappingService.updateMapping(id, request), "success"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mapping")
    public ResponseEntity<ApiResponse<Void>> deleteMapping(@PathVariable UUID id) {
        workFlowMappingService.deleteMapping(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Mapping deleted successfully"));
    }
}
