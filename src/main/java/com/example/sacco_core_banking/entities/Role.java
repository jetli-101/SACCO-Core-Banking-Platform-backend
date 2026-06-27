package com.example.sacco_core_banking.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A role group a User can belong to (ROLE_MEMBER, ROLE_TELLER, ROLE_ADMIN, ...). Global
 * catalogue, not scoped to a Sacco — see enums.RoleName for the fixed set seeded on
 * startup. Membership is via the explicit UserRole entity (not a direct @ManyToMany)
 * so membership rows have their own identity/CRUD. Drives which dashboard modules a
 * member of the group can see (via Role.modules) and the group's default permissions
 * (via RolePermission).
 */
@Entity
@Table(name = "smoothsurf_sacco_roles")
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "smoothsurf_sacco_role_modules",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id"))
    private Set<ModuleRegister> modules = new HashSet<>();

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
