package com.example.sacco_core_banking.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The default permission a role group has on a module — module is nullable to allow a
 * role-wide grant not tied to any one module. Distinct from UserModulePermission, which
 * is a per-user override on top of whatever the user's roles grant here.
 */
@Entity
@Table(name = "smoothsurf_sacco_role_permissions")
@Getter
@Setter
@NoArgsConstructor
public class RolePermission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private ModuleRegister moduleRegister;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
}
