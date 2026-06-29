package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.module.ModuleRegisterRequest;
import com.example.sacco_core_banking.dto.module.ModuleResponse;
import com.example.sacco_core_banking.dto.module.ModuleTreeResponse;
import com.example.sacco_core_banking.dto.role.RoleResponse;
import com.example.sacco_core_banking.services.ModuleRegisterService;
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
@RequestMapping(path = Constants.API_BASE_PATH + "/modules")
@Tag(name = "Module Management", description = "APIs for managing Modules")
public class ModuleRegisterController {

    @Autowired
    private ModuleRegisterService moduleRegisterService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping(value = "/all")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "List Modules", description = "This method is used to get all modules.")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> listModuleRegisters() {
        List<ModuleResponse> modules = moduleRegisterService.listModules();
        return ResponseEntity.ok(ApiResponse.success(modules, "success"));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Get a Module by ID", description = "This method is used to get a single module that matches the provided id.")
    public ResponseEntity<ApiResponse<ModuleResponse>> getModuleRegister(
            @Parameter(description = "ID of module being retrieved.", required = true)
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(ApiResponse.success(moduleRegisterService.getModuleById(id), "success"));
    }

    @GetMapping(value = "/{id}/roles")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Get roles granted access to a module", description = "Returns every role that currently has this module in its menu.")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getModuleRoles(
            @Parameter(description = "ID of the module.", required = true)
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(ApiResponse.success(moduleRegisterService.getRolesForModule(id), "success"));
    }

    @GetMapping("/module-tree")
    @Operation(summary = "Get the current user's menu", description = "Union of every module granted by the caller's role groups, grouped by module type.")
    public ResponseEntity<ApiResponse<List<ModuleTreeResponse>>> getModuleTree() {
        List<ModuleTreeResponse> moduleTree = moduleRegisterService.getMyMenu(currentUser.get());
        return ResponseEntity.ok(ApiResponse.success(moduleTree, "Module tree retrieved successfully"));
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Create Module", description = "This method is used to create a single module.")
    public ResponseEntity<ApiResponse<ModuleResponse>> createModuleRegister(@Valid @RequestBody ModuleRegisterRequest request) {
        ModuleResponse createdModule = moduleRegisterService.createModule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdModule, "success"));
    }

    @PutMapping(value = "/update/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Update a Module", description = "This method is used to edit a module.")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModuleRegister(
            @Parameter(description = "ID of module being updated.", required = true)
            @PathVariable(value = "id") UUID id,
            @Valid @RequestBody ModuleRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(moduleRegisterService.updateModule(id, request), "success"));
    }

    @DeleteMapping(path = "/delete/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Delete a Module", description = "This method is used to delete a single module that matches the provided id.")
    public ResponseEntity<ApiResponse<Void>> deleteModuleRegister(
            @Parameter(description = "ID of module being deleted.", required = true)
            @PathVariable UUID id) {
        moduleRegisterService.deleteModule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Module deleted successfully"));
    }
}
