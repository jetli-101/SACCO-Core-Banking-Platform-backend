package com.example.sacco_core_banking.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Per-user override of what a specific user can do on a specific module, independent
 * of their role groups — e.g. a teller who also needs APPROVE on "Pending Approvals"
 * without being promoted to branch manager. Role.modules controls visibility; this
 * controls the finer-grained CRUD actions once a module is visible.
 */
@Entity
@Table(name = "smoothsurf_sacco_user_module_permissions")
@Getter
@Setter
@NoArgsConstructor
public class UserModulePermission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleRegister moduleRegister;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "smoothsurf_sacco_user_module_permission_values",
            joinColumns = @JoinColumn(name = "user_module_permission_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
}
