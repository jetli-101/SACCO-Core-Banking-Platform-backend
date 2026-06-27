package com.example.sacco_core_banking.entities;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single navigable feature/menu item (e.g. "Member Registration", "Loan Approval").
 * Self-referencing parent/children so a module can have sub-items, same shape as the
 * frontend's /dashboard/* route tree. Which role groups can see a module lives on the
 * Role side (Role.modules) — a user's menu is the union of every group they belong to.
 */
@Entity
@Table(name = "smoothsurf_sacco_modules")
@Getter
@Setter
@NoArgsConstructor
public class ModuleRegister extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @NotBlank
    @Column(name = "text_id", nullable = false, unique = true)
    private String textId;

    @Column(name = "url_path")
    private String urlPath;

    private String icon;

    @Column(name = "order_no")
    private int orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_type_id")
    private ModuleType moduleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private ModuleRegister parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonManagedReference
    private List<ModuleRegister> children;

    @ManyToMany(mappedBy = "modules")
    private Set<Role> roles;
}
