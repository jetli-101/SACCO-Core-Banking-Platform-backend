package com.example.sacco_core_banking.dto.auth;

import java.time.LocalDate;

import com.example.sacco_core_banking.enums.Gender;
import com.example.sacco_core_banking.validation.KenyanPhone;
import com.example.sacco_core_banking.validation.KraPin;
import com.example.sacco_core_banking.validation.NationalId;
import com.example.sacco_core_banking.validation.ValidCounty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Member self-registration. Deliberately requires the full KYC set up front (national
 * ID, KRA PIN, county/constituency/ward, physical address, next of kin) rather than
 * letting members trickle in partial profiles — a Kenyan SACCO can't open an account,
 * issue a share certificate, or pay a dividend on a member record missing any of this,
 * and chasing it after approval is harder than asking for it once at the gate.
 */
@Data
public class RegisterMemberRequest {

    @NotBlank(message = "Sacco code is required")
    private String saccoCode;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    private String email;

    @KenyanPhone
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NationalId
    private String nationalId;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    @KraPin
    private String kraPin;

    private String employer;

    @ValidCounty
    private String county;

    @NotBlank(message = "Constituency is required")
    private String constituency;

    @NotBlank(message = "Ward is required")
    private String ward;

    private String postalAddress;

    @NotBlank(message = "Physical address is required")
    private String physicalAddress;

    @NotNull(message = "Next of kin details are required")
    @Valid
    private NextOfKinRequest nextOfKin;
}
