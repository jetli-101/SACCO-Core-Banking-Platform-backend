package com.example.sacco_core_banking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lifecycle state of a stage DEFINITION (DRAFT/ACTIVE/ARCHIVED — seeded by
 * WorkFlowStateSeeder), replacing a plain boolean so a stage can be authored as a draft or
 * retired as archived without deleting it. The engine only allows entry into a stage whose
 * state textId is "ACTIVE".
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_states")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlowState extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Column(name = "text_id", nullable = false, unique = true)
    private String textId;

    @Column(columnDefinition = "TEXT")
    private String description;
}
