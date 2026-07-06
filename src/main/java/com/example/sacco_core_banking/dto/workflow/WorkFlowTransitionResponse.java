package com.example.sacco_core_banking.dto.workflow;

import java.util.UUID;

import com.example.sacco_core_banking.enums.TransitionActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowTransitionResponse {
    private UUID id;
    private UUID workFlowId;
    private UUID fromStageId;
    private String fromStageName;
    private UUID toStageId;
    private String toStageName;
    private String name;
    private TransitionActionType actionType;
    private UUID resultingStatusId;
    private String resultingStatusName;
    private String description;
    private boolean active;
}
