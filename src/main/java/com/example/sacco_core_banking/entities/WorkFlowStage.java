package com.example.sacco_core_banking.entities;

import com.example.sacco_core_banking.enums.StageResponsibleType;
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
 * One ordered step in a WorkFlow (e.g. "Submission stage", "Review stage"). Who may act on
 * it depends on responsibleType: GROUP consults responsibleGroup, ROLE consults
 * responsibleRole, USERS consults the WorkFlowStageResponsibleUser join rows for this
 * stage, ANY means unrestricted. state (DRAFT/ACTIVE/ARCHIVED) is the single source of
 * truth for whether the engine may enter this stage — there is no separate boolean flag.
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_stages")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlowStage extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    @JsonIgnore
    private WorkFlow workFlow;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_no", nullable = false)
    private int orderNo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "responsible_type", nullable = false)
    private StageResponsibleType responsibleType = StageResponsibleType.ANY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_group_id")
    private UserGroup responsibleGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_role_id")
    private Role responsibleRole;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private WorkFlowState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_status_id")
    private WorkFlowStatus defaultStatus;

    @Column(name = "is_initial", nullable = false)
    private boolean isInitial;

    @Column(name = "is_final", nullable = false)
    private boolean isFinal;

    /**
     * Key into the frontend's interface registry — identifies which action-panel component
     * to render for an instance sitting at this stage (e.g. "CLAIM_REVIEW"). Null means no
     * custom interface is registered yet, so the frontend falls back to a generic action panel.
     */
    @Column(name = "interface_key")
    private String interfaceKey;
}
