package com.example.sacco_core_banking.dto.role;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRoleRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Role ID is required")
    private UUID roleId;
}
