package com.example.sacco_core_banking.dto.usergroup;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupResponse {
    private UUID id;
    private String name;
    private String description;
    private long memberCount;
}
