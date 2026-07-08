package com.example.sacco_core_banking.dto.workflow;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One workflow stage plus the checklists currently linked to it — powers the "Checklists" tab of the Workflow Checklist Linking screen. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageChecklistLinksResponse {
    private UUID stageId;
    private String stageName;
    private int orderNo;
    private List<ChecklistSummaryResponse> checklists;
}
