package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.permission.RolePermissionRequest;
import com.example.sacco_core_banking.dto.permission.RolePermissionResponse;
import com.example.sacco_core_banking.services.RolePermissionService;
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

@RestController
@RequestMapping(path = Constants.ROLE_PERMISSIONS_PATH)
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
@Tag(name = "Role Permission Management", description = "APIs for managing Role Permissions")
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    @PostMapping
    @Operation(summary = "Create a RolePermission", description = "Grants a role group a default permission on a module (or role-wide if moduleId is omitted).")
    public ResponseEntity<RolePermissionResponse> createRolePermission(@Valid @RequestBody RolePermissionRequest request) {
        return new ResponseEntity<>(rolePermissionService.createRolePermission(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get RolePermission by ID", description = "Retrieves a RolePermission by its UUID.")
    public ResponseEntity<RolePermissionResponse> getRolePermission(
            @Parameter(description = "ID of rolePermission retrieved.", required = true)
            @PathVariable("id") UUID id) {
        return new ResponseEntity<>(rolePermissionService.getRolePermissionById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "List All Role Permissions", description = "Lists all Role Permissions.")
    public List<RolePermissionResponse> listRolePermissions() {
        return rolePermissionService.findAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a RolePermission", description = "Updates an existing RolePermission based on its UUID.")
    public ResponseEntity<RolePermissionResponse> updateRolePermission(
            @Parameter(description = "ID of rolePermission being updated.", required = true)
            @PathVariable(value = "id") UUID id,
            @Valid @RequestBody RolePermissionRequest request) {
        return new ResponseEntity<>(rolePermissionService.updateRolePermission(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a RolePermission", description = "Deletes a RolePermission based on its UUID.")
    public ResponseEntity<Void> deleteRolePermission(
            @Parameter(description = "ID of rolePermission being deleted.", required = true)
            @PathVariable UUID id) {
        rolePermissionService.deleteRolePermission(id);
        return ResponseEntity.noContent().build();
    }
}
