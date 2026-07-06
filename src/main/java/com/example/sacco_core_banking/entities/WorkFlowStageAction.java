package com.example.sacco_core_banking.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Optional gating layer on top of a WorkFlowTransition: when present for a transition,
 * taking that transition additionally requires the linked checklist items to be completed
 * and/or a non-blank comment. A transition with no WorkFlowStageAction behaves exactly as
 * before (ungated). Deliberately does not duplicate the transition's own name/action-type/
 * status fields — those already live on WorkFlowTransition and are shown on the
 * Transitions tab.
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_stage_actions")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlowStageAction extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_id", nullable = false)
    private WorkFlowStage stage;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transition_id", nullable = false, unique = true)
    private WorkFlowTransition transition;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "requires_checklist_completion", nullable = false)
    private boolean requiresChecklistCompletion;

    @Column(name = "requires_approval_comment", nullable = false)
    private boolean requiresApprovalComment;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "stageAction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<WorkFlowStageActionChecklist> checklistItems;
}
