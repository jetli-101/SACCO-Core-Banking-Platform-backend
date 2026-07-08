package com.example.sacco_core_banking.dto.workflow;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Lightweight checklist reference — used as the chip shown against a stage on the Workflow Checklist Linking screen. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistSummaryResponse {
    private UUID id;
    private String name;
    private int itemCount;
    private int mandatoryCount;
}
