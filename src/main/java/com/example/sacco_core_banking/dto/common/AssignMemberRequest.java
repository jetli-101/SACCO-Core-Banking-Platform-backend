package com.example.sacco_core_banking.dto.common;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Adds one user to a Role or UserGroup's membership — used by both controllers. */
@Data
public class AssignMemberRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;
}
