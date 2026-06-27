package com.example.sacco_core_banking.entities;

import java.time.OffsetDateTime;

import com.example.sacco_core_banking.enums.ApprovalDecision;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One row per approve/reject/more-info decision made on a Member's registration.
 * Status on the linked User changes as a side effect, but this table is the durable
 * audit trail of who decided what, and why.
 */
@Entity
@Table(name = "smoothsurf_sacco_member_approvals")
@Getter
@Setter
@NoArgsConstructor
public class MemberApproval extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approved_by_id", nullable = false)
    private User approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalDecision decision;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "decided_at", nullable = false)
    private OffsetDateTime decidedAt;
}
