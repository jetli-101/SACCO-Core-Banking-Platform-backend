package com.example.sacco_core_banking.dto.module;

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
public class ModuleResponse {
    private UUID id;
    private String name;
    private String textId;
    private String description;
    private String urlPath;
    private String icon;
    private int orderNo;
    private UUID moduleTypeId;
    private UUID parentId;
    private List<ModuleResponse> children;
}
