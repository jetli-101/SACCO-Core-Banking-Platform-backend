package com.example.sacco_core_banking.dto.module;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleTypeResponse {
    private UUID id;
    private String name;
    private String textId;
    private int orderNo;
    private String description;
}
