package com.example.sacco_core_banking.dto.member;

import com.example.sacco_core_banking.validation.ValidCounty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Self-service profile update. Deliberately excludes identity fields (nationalId,
 * names, dateOfBirth, gender) — those are KYC data set at registration/approval time
 * and changing them requires an admin-handled correction, not a member self-edit.
 */
@Data
public class UpdateMemberRequest {

    @NotBlank(message = "Occupation is required")
    private String occupation;

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
}
