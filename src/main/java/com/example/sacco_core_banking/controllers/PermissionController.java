package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.permission.PermissionRequest;
import com.example.sacco_core_banking.dto.permission.PermissionResponse;
import com.example.sacco_core_banking.services.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping(path = Constants.PERMISSIONS_PATH)
@RequiredArgsConstructor
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
@Tag(name = "Permission Management", description = "APIs for managing Permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Create a Permission", description = "Creates a new Permission.")
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request) {
        return new ResponseEntity<>(permissionService.createPermission(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Permission by ID", description = "Retrieves a Permission by its UUID.")
    public ResponseEntity<PermissionResponse> getPermission(
            @Parameter(description = "ID of permission retrieved.", required = true)
            @PathVariable("id") UUID id) {
        return new ResponseEntity<>(permissionService.getPermissionById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "List All Permissions", description = "Lists all Permissions.")
    public List<PermissionResponse> listPermissions() {
        return permissionService.findAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Permission", description = "Updates an existing Permission based on its UUID.")
    public ResponseEntity<PermissionResponse> updatePermission(
            @Parameter(description = "ID of permission being updated.", required = true)
            @PathVariable(value = "id") UUID id,
            @Valid @RequestBody PermissionRequest request) {
        return new ResponseEntity<>(permissionService.updatePermission(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Permission", description = "Deletes a Permission based on its UUID.")
    public ResponseEntity<Void> deletePermission(
            @Parameter(description = "ID of permission being deleted.", required = true)
            @PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
