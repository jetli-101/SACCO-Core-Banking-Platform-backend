package com.example.sacco_core_banking.dto.module;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One nav section (a ModuleType) with the modules the current user can see under it. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleTreeResponse {
    private UUID moduleTypeId;
    private String moduleTypeName;
    private int orderNo;
    private List<ModuleResponse> modules;
}
