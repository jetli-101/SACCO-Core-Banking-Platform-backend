package com.example.sacco_core_banking.dto.admin;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.sacco_core_banking.enums.ApprovalDecision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberApprovalResponse {
    private UUID id;
    private UUID memberId;
    private String memberName;
    private String memberNumber;
    private ApprovalDecision decision;
    private String comments;
    private String decidedBy;
    private OffsetDateTime decidedAt;
}
