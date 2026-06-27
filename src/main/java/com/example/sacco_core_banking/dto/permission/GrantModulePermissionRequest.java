package com.example.sacco_core_banking.dto.permission;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GrantModulePermissionRequest {

    @NotNull(message = "Module ID is required")
    private UUID moduleId;

    @NotEmpty(message = "At least one permission is required")
    private Set<String> permissionNames;
}
