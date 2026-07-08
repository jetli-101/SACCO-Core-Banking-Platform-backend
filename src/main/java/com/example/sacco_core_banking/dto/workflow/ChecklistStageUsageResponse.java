package com.example.sacco_core_banking.dto.workflow;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One stage that a given checklist is linked to — used by the checklist detail screen and the "unlink" action. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistStageUsageResponse {
    private UUID stageId;
    private String stageName;
    private int stageOrderNo;
    private UUID workFlowId;
    private String workFlowName;
}
