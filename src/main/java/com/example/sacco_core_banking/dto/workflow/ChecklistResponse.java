package com.example.sacco_core_banking.dto.workflow;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistResponse {
    private UUID id;
    private String name;
    private String description;
    private boolean active;
    private List<ChecklistItemResponse> items;
    private int totalItems;
    private int mandatoryItems;
    private int usedInStagesCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
