package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.module.ModuleTypeRequest;
import com.example.sacco_core_banking.dto.module.ModuleTypeResponse;
import com.example.sacco_core_banking.services.ModuleTypeService;
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
@RequestMapping(path = Constants.API_BASE_PATH + "/module-types")
@RequiredArgsConstructor
@Tag(name = "Module Types", description = "Module Types API end-points.")
public class ModuleTypeController {

    private final ModuleTypeService moduleTypeService;

    @GetMapping(value = "/all")
    @Operation(summary = "List Module Types", description = "This method is used to get all module types.")
    public ResponseEntity<ApiResponse<List<ModuleTypeResponse>>> listModuleTypes() {
        List<ModuleTypeResponse> moduleTypes = moduleTypeService.listModuleTypes();
        return ResponseEntity.ok(ApiResponse.success(moduleTypes, "Retrieved all module types successfully"));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get a Module Type by ID", description = "This method is used to get a single module type that matches the provided id.")
    public ResponseEntity<ApiResponse<ModuleTypeResponse>> getModuleType(
            @Parameter(description = "ID of module type being retrieved.", required = true)
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(ApiResponse.success(moduleTypeService.getModuleTypeById(id), "Module type retrieved successfully"));
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Create Module Type", description = "This method is used to create a single module type.")
    public ResponseEntity<ApiResponse<ModuleTypeResponse>> createModuleType(@Valid @RequestBody ModuleTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(moduleTypeService.createModuleType(request), "Module type created successfully"));
    }

    @PutMapping(value = "/update/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Update a Module Type", description = "This method is used to edit a module type.")
    public ResponseEntity<ApiResponse<ModuleTypeResponse>> updateModuleType(
            @Parameter(description = "ID of module type being updated.", required = true)
            @PathVariable(value = "id") UUID id,
            @Valid @RequestBody ModuleTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(moduleTypeService.updateModuleType(id, request), "Module type updated successfully"));
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Delete a Module Type", description = "This method is used to delete a single module type that matches the provided id.")
    public ResponseEntity<ApiResponse<Void>> deleteModuleType(
            @Parameter(description = "ID of module type being deleted.", required = true)
            @PathVariable UUID id) {
        moduleTypeService.deleteModuleType(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Module type deleted successfully"));
    }
}
