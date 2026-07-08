package com.example.sacco_core_banking.dto.workflow;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Forward-to-person: reassigns an instance to a specific user without changing its stage. */
@Data
public class ReassignWorkFlowInstanceRequest {

    @NotNull(message = "The user to forward to is required")
    private UUID assignedToUserId;

    private String comment;
}
