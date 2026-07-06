package com.example.sacco_core_banking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Shared catalog of business-facing statuses ("Submitted", "Reviewed", "Approved",
 * "Rejected", "Completed"). Referenced by WorkFlowTransition.resultingStatus (the status a
 * process takes on after that transition fires) and WorkFlowStage.defaultStatus (the
 * status a process starts with when it enters a stage directly, e.g. the initial stage).
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_statuses")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlowStatus extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Column(name = "text_id", nullable = false, unique = true)
    private String textId;

    @Column(columnDefinition = "TEXT")
    private String description;
}
