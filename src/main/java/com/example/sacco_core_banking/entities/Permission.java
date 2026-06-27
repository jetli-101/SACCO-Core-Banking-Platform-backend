package com.example.sacco_core_banking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A grantable action (CREATE, READ, UPDATE, DELETE, APPROVE, ...). A managed catalogue
 * rather than a fixed enum so an admin can introduce a new action without a code
 * change — see PermissionSeeder for the default set seeded on startup.
 */
@Entity
@Table(name = "smoothsurf_sacco_permissions")
@Getter
@Setter
@NoArgsConstructor
public class Permission extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
