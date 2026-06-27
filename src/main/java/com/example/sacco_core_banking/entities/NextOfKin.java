package com.example.sacco_core_banking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "smoothsurf_sacco_next_of_kin")
@Getter
@Setter
@NoArgsConstructor
public class NextOfKin extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String relationship;

    private String phone;

    @Column(name = "id_number")
    private String idNumber;

    private String address;
}
