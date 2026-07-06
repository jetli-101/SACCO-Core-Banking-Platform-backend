package com.example.sacco_core_banking.dto.workflow;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.enums.StageResponsibleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkFlowStageRequest {

    @NotNull(message = "WorkFlow is required")
    private UUID workFlowId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private int orderNo;

    @NotNull(message = "Responsible type is required")
    private StageResponsibleType responsibleType = StageResponsibleType.ANY;

    /** Required when responsibleType = GROUP. */
    private UUID responsibleGroupId;

    /** Required when responsibleType = ROLE. */
    private UUID responsibleRoleId;

    /** Required when responsibleType = USERS. */
    private List<UUID> responsibleUserIds;

    @NotNull(message = "State is required")
    private UUID stateId;

    private UUID defaultStatusId;

    private boolean isInitial;

    private boolean isFinal;
}
