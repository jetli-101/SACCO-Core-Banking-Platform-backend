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
 * A workflow/approval grouping a User can belong to (e.g. "Finance Approvers"),
 * distinct from Role: Role drives backend security checks (hasRole) and the legacy
 * sidebar grant, UserGroup is a second, independent way to grant module access —
 * a module is visible to a user if EITHER any of their Roles OR any of their
 * UserGroups grants it (see ModuleRegisterService.getMyMenu). Membership is via the
 * explicit UserGroupMember entity, same pattern as Role/UserRole.
 */
@Entity
@Table(name = "smoothsurf_sacco_user_groups")
@Getter
@Setter
@NoArgsConstructor
public class UserGroup extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "smoothsurf_sacco_user_group_modules",
            joinColumns = @JoinColumn(name = "user_group_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id"))
    private Set<ModuleRegister> modules = new HashSet<>();

    public UserGroup(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
