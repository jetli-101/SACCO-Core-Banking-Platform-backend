package com.example.sacco_core_banking.dto.auth;

import com.example.sacco_core_banking.validation.KenyanPhone;
import com.example.sacco_core_banking.validation.NationalId;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Every SACCO registration form in Kenya collects next-of-kin details up front (it's
 * required for beneficiary payout/death-claim processing), so this is mandatory at
 * registration time rather than something added later.
 */
@Data
public class NextOfKinRequest {

    @NotBlank(message = "Next of kin name is required")
    private String name;

    @NotBlank(message = "Next of kin relationship is required")
    private String relationship;

    @KenyanPhone
    private String phone;

    @NationalId
    private String idNumber;

    @NotBlank(message = "Next of kin address is required")
    private String address;
}
