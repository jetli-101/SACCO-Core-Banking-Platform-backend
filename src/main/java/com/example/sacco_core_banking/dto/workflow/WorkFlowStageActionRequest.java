package com.example.sacco_core_banking.dto.workflow;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkFlowStageActionRequest {

    @NotNull(message = "Stage is required")
    private UUID stageId;

    @NotNull(message = "Transition is required")
    private UUID transitionId;

    @NotBlank(message = "Name is required")
    private String name;

    private boolean requiresChecklistCompletion;

    private boolean requiresApprovalComment;

    private boolean active = true;

    @Valid
    private List<WorkFlowStageActionChecklistRequest> checklistItems;
}
