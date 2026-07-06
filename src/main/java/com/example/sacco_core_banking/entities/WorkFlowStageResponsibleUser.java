package com.example.sacco_core_banking.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One row per user explicitly assigned to act on a stage — used when the stage's
 * responsibleType is USERS. Explicit join entity (same pattern as UserGroupMember/UserRole
 * in this codebase) rather than a bare @ManyToMany, so membership has its own identity.
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_stage_responsible_users")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlowStageResponsibleUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_id", nullable = false)
    private WorkFlowStage stage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public WorkFlowStageResponsibleUser(WorkFlowStage stage, User user) {
        this.stage = stage;
        this.user = user;
    }
}
