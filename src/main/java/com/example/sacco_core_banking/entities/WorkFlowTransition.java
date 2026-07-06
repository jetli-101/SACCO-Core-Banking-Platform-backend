package com.example.sacco_core_banking.entities;

import com.example.sacco_core_banking.enums.TransitionActionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An allowed move from one WorkFlowStage to another within the same WorkFlow, e.g.
 * "Submission stage" --Submit to review--> "Review stage". resultingStatus is the
 * business-facing status ("Submitted", "Reviewed") the process takes on once this
 * transition fires — distinct from the instance-level WorkFlowInstanceStatus used for
 * dashboard filtering.
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_transitions")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlowTransition extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    @JsonIgnore
    private WorkFlow workFlow;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_stage_id", nullable = false)
    private WorkFlowStage fromStage;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_stage_id", nullable = false)
    private WorkFlowStage toStage;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private TransitionActionType actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resulting_status_id")
    private WorkFlowStatus resultingStatus;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
