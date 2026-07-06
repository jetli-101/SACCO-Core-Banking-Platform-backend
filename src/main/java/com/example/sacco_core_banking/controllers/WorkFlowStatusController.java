package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStatusRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStatusResponse;
import com.example.sacco_core_banking.services.WorkFlowStatusService;
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

/** Admin CRUD for the shared WorkFlowStatus catalog ("Submitted", "Reviewed", "Approved", ...). */
@RestController
@RequestMapping(path = Constants.WORKFLOW_STATUSES_PATH)
@Tag(name = "Workflow Status Management", description = "APIs for managing the shared workflow status catalog")
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
public class WorkFlowStatusController {

    @Autowired
    private WorkFlowStatusService workFlowStatusService;

    @GetMapping
    @Operation(summary = "List statuses")
    public ResponseEntity<ApiResponse<List<WorkFlowStatusResponse>>> listStatuses() {
        return ResponseEntity.ok(ApiResponse.success(workFlowStatusService.listStatuses(), "success"));
    }

    @PostMapping
    @Operation(summary = "Create a status")
    public ResponseEntity<ApiResponse<WorkFlowStatusResponse>> createStatus(@Valid @RequestBody WorkFlowStatusRequest request) {
        WorkFlowStatusResponse created = workFlowStatusService.createStatus(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Status created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a status")
    public ResponseEntity<ApiResponse<WorkFlowStatusResponse>> updateStatus(
            @PathVariable UUID id, @Valid @RequestBody WorkFlowStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(workFlowStatusService.updateStatus(id, request), "success"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a status")
    public ResponseEntity<ApiResponse<Void>> deleteStatus(@PathVariable UUID id) {
        workFlowStatusService.deleteStatus(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Status deleted successfully"));
    }
}
