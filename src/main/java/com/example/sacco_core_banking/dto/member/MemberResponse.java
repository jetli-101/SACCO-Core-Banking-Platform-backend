package com.example.sacco_core_banking.dto.member;

import java.time.LocalDate;
import java.util.UUID;

import com.example.sacco_core_banking.dto.user.UserResponse;
import com.example.sacco_core_banking.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private UUID id;
    private String memberNumber;
    private String nationalId;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String occupation;
    private String kraPin;
    private String employer;
    private String county;
    private String constituency;
    private String ward;
    private String postalAddress;
    private String physicalAddress;
    private String passportPhotoUrl;
    private String signatureUrl;
    private NextOfKinResponse nextOfKin;
    private UserResponse user;
}
