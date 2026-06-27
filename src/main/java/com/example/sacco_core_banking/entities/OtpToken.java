package com.example.sacco_core_banking.entities;

import java.time.OffsetDateTime;

import com.example.sacco_core_banking.enums.OtpPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A one-time code emailed to a user for a specific purpose (registration email
 * verification, password reset, ...). Each new send invalidates prior unused codes
 * for the same user+purpose — see OtpService.
 */
@Entity
@Table(name = "smoothsurf_sacco_otp_tokens")
@Getter
@Setter
@NoArgsConstructor
public class OtpToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;
}
