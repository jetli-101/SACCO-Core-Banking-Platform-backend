package com.example.sacco_core_banking.entities;

import com.example.sacco_core_banking.enums.SaccoStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A tenant on this platform. Every User and Member belongs to exactly one Sacco.
 */
@Entity
@Table(name = "smoothsurf_sacco_saccos")
@Getter
@Setter
@NoArgsConstructor
public class Sacco extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaccoStatus status = SaccoStatus.ACTIVE;
}
