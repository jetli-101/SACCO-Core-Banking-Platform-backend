package com.example.sacco_core_banking.entities;

import java.time.LocalDate;

import com.example.sacco_core_banking.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SACCO-specific profile for a member. A User logs in; this is the data that belongs
 * to the SACCO relationship itself. memberNumber is only assigned once the
 * registration is approved (see MemberApproval).
 */
@Entity
@Table(name = "smoothsurf_sacco_members")
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sacco_id", nullable = false)
    private Sacco sacco;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "member_number", unique = true)
    private String memberNumber;

    @NotBlank
    @Column(name = "national_id", nullable = false)
    private String nationalId;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String occupation;

    @Column(name = "kra_pin")
    private String kraPin;

    private String employer;

    private String county;

    private String constituency;

    private String ward;

    @Column(name = "postal_address")
    private String postalAddress;

    @Column(name = "physical_address")
    private String physicalAddress;

    @Column(name = "passport_photo_url", columnDefinition = "TEXT")
    private String passportPhotoUrl;

    @Column(name = "signature_url", columnDefinition = "TEXT")
    private String signatureUrl;
}
