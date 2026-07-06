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
public class WorkFlowInstanceHistoryResponse {
    private UUID id;
    private UUID fromStageId;
    private String fromStageName;
    private UUID toStageId;
    private String toStageName;
    private UUID transitionId;
    private String transitionName;
    private String comment;
    private UUID performedByUserId;
    private String performedByName;
    private OffsetDateTime performedAt;
    private Map<String, Object> dataSnapshot;
}
