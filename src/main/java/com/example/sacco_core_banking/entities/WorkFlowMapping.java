package com.example.sacco_core_banking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * The plug-in point: registers which WorkFlow a calling module's process type resolves to
 * (e.g. "CLAIM" -> Claim WorkFlow, "LEAVE" -> Leave WorkFlow). WorkFlowEngineService looks
 * this up by processTypeKey so a new module can attach to the engine without adding any
 * new tables of its own.
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_mappings")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlowMapping extends BaseEntity {

    @NotBlank
    @Column(name = "process_type_key", nullable = false, unique = true)
    private String processTypeKey;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    private WorkFlow workFlow;

    @Column(nullable = false)
    private boolean active = true;
}
