package com.example.sacco_core_banking.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Top-level grouping for the dashboard nav (e.g. "Membership", "Loans", "Administration")
 * — mirrors the section headers in the frontend's sidebarNav config, so the menu can
 * eventually be served from here instead of hard-coded on the client.
 */
@Entity
@Table(name = "smoothsurf_sacco_module_types")
@Getter
@Setter
@NoArgsConstructor
public class ModuleType extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Column(name = "text_id", nullable = false, unique = true)
    private String textId;

    @Column(name = "order_no")
    private int orderNo;

    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "moduleType")
    private List<ModuleRegister> modules;
}
