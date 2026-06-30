package com.example.sacco_core_banking.dto.user;

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
public class UserResponse {
    private UUID id;
    private UUID saccoId;
    private String saccoName;
    private String username;
    private String email;
    private String phone;
    private List<String> roles;
    private String status;
    private String avatarUrl;
    private UUID memberId;
    private OffsetDateTime lastLoginAt;
    private OffsetDateTime createdAt;
}
