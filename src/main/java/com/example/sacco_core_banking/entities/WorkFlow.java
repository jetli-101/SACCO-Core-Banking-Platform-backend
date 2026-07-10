package com.example.sacco_core_banking.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A reusable approval-process template: an ordered set of WorkFlowStages connected by
 * WorkFlowTransitions. Any module attaches to one via WorkFlowMapping instead of building
 * its own approval tables.
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_workflows")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlow extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    /**
     * Extra roles (beyond ROLE_SYSTEM_ADMINISTRATOR, which can always see/manage every
     * workflow) allowed to view and configure this specific workflow — e.g. letting Branch
     * Managers own the Loan Application Approval workflow without exposing every other
     * workflow in the system to them. Empty means admin-only (the original, unrestricted
     * default).
     */
    @ManyToMany
    @JoinTable(
            name = "smoothsurf_sacco_wf_workflow_roles",
            joinColumns = @JoinColumn(name = "workflow_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> allowedRoles = new HashSet<>();

    @OneToMany(mappedBy = "workFlow", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderNo ASC")
    @JsonIgnore
    private List<WorkFlowStage> stages;

    @OneToMany(mappedBy = "workFlow", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<WorkFlowTransition> transitions;
}
