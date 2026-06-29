package com.example.sacco_core_banking.dto.common;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A user's membership row in a Role or UserGroup's "Members" tab. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberResponse {
    private UUID id;
    private UUID userId;
    private String username;
    private String email;
}
