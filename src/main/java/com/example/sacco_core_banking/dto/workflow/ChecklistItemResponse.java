package com.example.sacco_core_banking.dto.workflow;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItemResponse {
    private UUID id;
    private UUID checklistId;
    private String itemText;
    private boolean required;
    private int orderNo;
}
