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
 * One row per role group a user belongs to. Explicit join entity (rather than a bare
 * @ManyToMany) so membership has its own identity/timestamps and can be managed through
 * its own CRUD endpoint, not just as a side effect of editing a User.
 */
@Entity
@Table(name = "smoothsurf_sacco_user_roles")
@Getter
@Setter
@NoArgsConstructor
public class UserRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }
}
