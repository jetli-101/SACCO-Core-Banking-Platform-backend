package com.example.sacco_core_banking.dto.workflow;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One row in the caller's Out-Tray: a process instance they have personally acted on,
 * annotated with the most recent action they took on it (WorkFlowInstanceHistory entry
 * where performedBy = caller). One row per instance, even if they've acted on it more than once.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowOutTrayItemResponse {
    private UUID instanceId;
    private String processCode;
    private String processName;
    private UUID workFlowId;
    private String workFlowName;
    private UUID referenceId;
    private String currentStageName;
    private String status;
    private String priority;
    private OffsetDateTime dueDate;
    private OffsetDateTime initiatedAt;

    private UUID historyId;
    private String fromStageName;
    private String toStageName;
    private String transitionName;
    private String comment;
    private OffsetDateTime performedAt;
}
