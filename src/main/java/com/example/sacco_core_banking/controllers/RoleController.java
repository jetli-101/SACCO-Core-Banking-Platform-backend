package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.module.ModuleResponse;
import com.example.sacco_core_banking.dto.role.RoleRequest;
import com.example.sacco_core_banking.dto.role.RoleResponse;
import com.example.sacco_core_banking.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(Constants.ROLES_PATH)
@RequiredArgsConstructor
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
@Tag(name = "Roles", description = "Role group catalogue")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "List roles", description = "Returns every role group in the catalogue.")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> listRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleService.listRoles(), "Roles retrieved"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a role by ID")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRoleById(id), "Role retrieved"));
    }

    @PostMapping
    @Operation(summary = "Create a role")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Role created"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a role")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(roleService.updateRole(id, request), "Role updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role", description = "Fails if the role is still assigned to any user.")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deleted"));
    }

    @GetMapping("/{roleId}/modules")
    @Operation(summary = "Get modules granted to a role")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesByRoleId(@PathVariable UUID roleId) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getModulesByRoleId(roleId), "Modules retrieved"));
    }

    @PostMapping("/{roleId}/modules")
    @Operation(summary = "Grant modules to a role", description = "Adds the given modules to whatever the role can already see.")
    public ResponseEntity<ApiResponse<RoleResponse>> assignModulesToRole(@PathVariable UUID roleId,
            @RequestBody Set<UUID> moduleIds) {
        return ResponseEntity.ok(ApiResponse.success(roleService.assignModulesToRole(roleId, moduleIds), "Modules assigned"));
    }

    @DeleteMapping("/{roleId}/modules")
    @Operation(summary = "Revoke modules from a role")
    public ResponseEntity<ApiResponse<RoleResponse>> unassignModulesFromRole(@PathVariable UUID roleId,
            @RequestBody Set<UUID> moduleIds) {
        return ResponseEntity.ok(ApiResponse.success(roleService.unassignModulesFromRole(roleId, moduleIds), "Modules unassigned"));
    }
}
