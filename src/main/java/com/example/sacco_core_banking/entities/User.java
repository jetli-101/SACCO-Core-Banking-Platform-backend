package com.example.sacco_core_banking.entities;

import java.time.OffsetDateTime;

import com.example.sacco_core_banking.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Authentication identity. A User logs in; a Member (if any) holds the SACCO-specific
 * profile. `enabled` and `locked` are derived from `status` rather than stored
 * separately, so there is exactly one source of truth for account state.
 */
@Entity
@Table(name = "smoothsurf_sacco_users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sacco_id", nullable = false)
    private Sacco sacco;

    @Column(unique = true)
    private String username;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String phone;

    @NotBlank
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

    @JsonIgnore
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return status != UserStatus.LOCKED && status != UserStatus.SUSPENDED;
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return status != UserStatus.DEACTIVATED;
    }
}
