package com.example.sacco_core_banking.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Join row linking a reusable Checklist to one WorkFlowStage — the many-to-many relationship
 * that lets the same checklist be reused across several stages/workflows. Explicit join
 * entity (same pattern as WorkFlowStageResponsibleUser) rather than a bare @ManyToMany, so
 * the link has its own identity/timestamp.
 */
@Entity
@Table(name = "smoothsurf_sacco_checklist_stage_links", uniqueConstraints = @UniqueConstraint(columnNames = {"checklist_id", "stage_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ChecklistStageLink extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_id", nullable = false)
    private WorkFlowStage stage;

    public ChecklistStageLink(Checklist checklist, WorkFlowStage stage) {
        this.checklist = checklist;
        this.stage = stage;
    }
}
