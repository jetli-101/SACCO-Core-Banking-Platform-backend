package com.example.sacco_core_banking.dto.workflow;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.enums.StageResponsibleType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowStageResponse {
    private UUID id;
    private UUID workFlowId;
    private String name;
    private String description;
    private int orderNo;
    private StageResponsibleType responsibleType;
    private UUID responsibleGroupId;
    private String responsibleGroupName;
    private UUID responsibleRoleId;
    private String responsibleRoleName;
    private List<UUID> responsibleUserIds;
    private UUID stateId;
    private String stateName;
    private UUID defaultStatusId;
    private String defaultStatusName;
    /** See WorkFlowStageRequest for why this needs an explicit @JsonProperty. */
    @JsonProperty("isInitial")
    private boolean isInitial;
    @JsonProperty("isFinal")
    private boolean isFinal;
    private String interfaceKey;
}
