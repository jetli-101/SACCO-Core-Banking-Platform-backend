package com.example.sacco_core_banking.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A reusable, standalone checklist (e.g. "Claims checklist") — unlike WorkFlowStageAction,
 * it isn't owned by a single stage/transition. It's created once and then linked to
 * whichever workflow stages need it via ChecklistStageLink, so the same checklist can be
 * reused across multiple stages and even multiple workflows.
 */
@Entity
@Table(name = "smoothsurf_sacco_checklists")
@Getter
@Setter
@NoArgsConstructor
public class Checklist extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderNo ASC")
    @JsonIgnore
    private List<ChecklistItem> items;
}
