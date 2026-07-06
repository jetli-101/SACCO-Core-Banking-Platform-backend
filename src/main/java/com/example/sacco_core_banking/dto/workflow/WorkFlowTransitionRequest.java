package com.example.sacco_core_banking.dto.workflow;

import java.util.UUID;

import com.example.sacco_core_banking.enums.TransitionActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkFlowTransitionRequest {

    @NotNull(message = "WorkFlow is required")
    private UUID workFlowId;

    @NotNull(message = "From stage is required")
    private UUID fromStageId;

    @NotNull(message = "To stage is required")
    private UUID toStageId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Action type is required")
    private TransitionActionType actionType;

    private UUID resultingStatusId;

    private String description;

    private boolean active = true;
}
