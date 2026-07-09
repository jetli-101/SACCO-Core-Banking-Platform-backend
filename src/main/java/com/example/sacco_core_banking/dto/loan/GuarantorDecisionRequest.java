package com.example.sacco_core_banking.dto.loan;

import com.example.sacco_core_banking.enums.GuarantorStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** The guarantor's own accept/decline response — decision must be ACCEPTED or DECLINED. */
@Data
public class GuarantorDecisionRequest {

    @NotNull(message = "Decision is required")
    private GuarantorStatus decision;
}
