package com.example.sacco_core_banking.dto.workflow;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowInstanceResponse {
    private UUID id;
    private String processCode;
    private String processName;
    private UUID workFlowId;
    private String workFlowName;
    private String processTypeKey;
    private UUID referenceId;
    private UUID currentStageId;
    private String currentStageName;
    /** WorkFlowStage.interfaceKey for the current stage — which action-panel component the frontend should render. */
    private String currentStageInterfaceKey;
    private UUID currentStatusId;
    private String currentStatusName;
    private String status;
    private String priority;
    private UUID assignedToUserId;
    private String assignedToName;
    private OffsetDateTime dueDate;
    /** Negative once overdue, matching the "-3 days remaining" style on the Instances screen. */
    private Long daysRemaining;
    private UUID initiatedByUserId;
    private String initiatedByName;
    private OffsetDateTime initiatedAt;
    /** Last time the instance moved stage — used to compute completion time once status is COMPLETED. */
    private OffsetDateTime updatedAt;
    private Map<String, Object> data;
}
