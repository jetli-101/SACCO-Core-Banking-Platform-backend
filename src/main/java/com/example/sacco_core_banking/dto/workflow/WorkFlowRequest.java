package com.example.sacco_core_banking.dto.workflow;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkFlowRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private boolean active = true;

    /** Roles (beyond ROLE_SYSTEM_ADMINISTRATOR) allowed to view/configure this workflow. Empty/null means admin-only. */
    private List<UUID> allowedRoleIds;
}
