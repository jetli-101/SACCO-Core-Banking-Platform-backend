package com.example.sacco_core_banking.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.loan.GuarantorDecisionRequest;
import com.example.sacco_core_banking.dto.loan.GuarantorRequest;
import com.example.sacco_core_banking.dto.loan.GuarantorResponse;
import com.example.sacco_core_banking.entities.Guarantor;
import com.example.sacco_core_banking.entities.Loan;
import com.example.sacco_core_banking.entities.Member;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.enums.GuarantorStatus;
import com.example.sacco_core_banking.repositories.GuarantorRepository;
import com.example.sacco_core_banking.repositories.LoanRepository;
import com.example.sacco_core_banking.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Guarantor invitations on a loan application. Only the applicant can add/remove
 * guarantors on their own loan; only the invited member can accept/decline their own
 * invitation — enforced here rather than with @PreAuthorize since both checks need to
 * compare against a specific row, not just a role.
 */
@Service
@Transactional
public class GuarantorService {

    @Autowired
    private GuarantorRepository guarantorRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private MemberRepository memberRepository;

    public List<GuarantorResponse> listGuarantors(UUID loanId) {
        return guarantorRepository.findByLoanId(loanId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public GuarantorResponse addGuarantor(UUID loanId, GuarantorRequest request, User caller) {
        Loan loan = findLoan(loanId);
        Member callerMember = currentMember(caller);
        if (!loan.getMember().getId().equals(callerMember.getId())) {
            throw new AccessDeniedException("Only the applicant can add guarantors to this loan");
        }
        if (request.getMemberId().equals(callerMember.getId())) {
            throw new IllegalArgumentException("You cannot guarantee your own loan");
        }

        Member guarantorMember = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Guarantor guarantor = new Guarantor();
        guarantor.setLoan(loan);
        guarantor.setMember(guarantorMember);
        guarantor.setGuaranteedAmount(request.getGuaranteedAmount());
        guarantor.setStatus(GuarantorStatus.PENDING);

        return toResponse(guarantorRepository.save(guarantor));
    }

    public GuarantorResponse respond(UUID guarantorId, GuarantorDecisionRequest request, User caller) {
        Guarantor guarantor = guarantorRepository.findById(guarantorId)
                .orElseThrow(() -> new ResourceNotFoundException("Guarantor invitation not found"));
        Member callerMember = currentMember(caller);
        if (!guarantor.getMember().getId().equals(callerMember.getId())) {
            throw new AccessDeniedException("Only the invited guarantor can respond to this invitation");
        }
        if (request.getDecision() == GuarantorStatus.PENDING) {
            throw new IllegalArgumentException("Decision must be ACCEPTED or DECLINED");
        }

        guarantor.setStatus(request.getDecision());
        guarantor.setRespondedAt(OffsetDateTime.now());
        return toResponse(guarantorRepository.save(guarantor));
    }

    public void removeGuarantor(UUID guarantorId, User caller) {
        Guarantor guarantor = guarantorRepository.findById(guarantorId)
                .orElseThrow(() -> new ResourceNotFoundException("Guarantor invitation not found"));
        Member callerMember = currentMember(caller);
        if (!guarantor.getLoan().getMember().getId().equals(callerMember.getId())) {
            throw new AccessDeniedException("Only the applicant can remove guarantors from this loan");
        }
        guarantorRepository.delete(guarantor);
    }

    private Loan findLoan(UUID loanId) {
        return loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
    }

    private Member currentMember(User user) {
        return memberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No member profile found for this user"));
    }

    private GuarantorResponse toResponse(Guarantor guarantor) {
        Member member = guarantor.getMember();
        return GuarantorResponse.builder()
                .id(guarantor.getId())
                .loanId(guarantor.getLoan().getId())
                .memberId(member.getId())
                .memberName(member.getFirstName() + " " + member.getLastName())
                .memberNumber(member.getMemberNumber())
                .memberAvatarUrl(member.getPassportPhotoUrl())
                .guaranteedAmount(guarantor.getGuaranteedAmount())
                .status(guarantor.getStatus().name())
                .respondedAt(guarantor.getRespondedAt())
                .build();
    }
}
