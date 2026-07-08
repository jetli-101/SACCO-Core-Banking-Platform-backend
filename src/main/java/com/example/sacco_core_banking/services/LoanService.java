package com.example.sacco_core_banking.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.loan.LoanRequest;
import com.example.sacco_core_banking.dto.loan.LoanResponse;
import com.example.sacco_core_banking.dto.workflow.StartWorkFlowRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowInstanceResponse;
import com.example.sacco_core_banking.entities.Loan;
import com.example.sacco_core_banking.entities.Member;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.enums.WorkFlowPriority;
import com.example.sacco_core_banking.repositories.LoanRepository;
import com.example.sacco_core_banking.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Loans module — the first real business domain wired to the workflow engine.
 * Applying for a loan creates the Loan record, then immediately attaches it to the engine
 * via WorkFlowEngineService.startProcess(processTypeKey="LOAN"), same entry point any
 * module uses. A WorkFlowMapping for "LOAN" must exist (created through the Workflows admin
 * UI) before an application can be submitted.
 */
@Service
@Transactional
public class LoanService {

    private static final String PROCESS_TYPE_KEY = "LOAN";

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WorkFlowEngineService workFlowEngineService;
    @Autowired
    private WorkFlowInstanceService workFlowInstanceService;

    public LoanResponse applyForLoan(LoanRequest request, User applicant) {
        Member member = memberRepository.findByUserId(applicant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No member profile found for this user"));

        OffsetDateTime now = OffsetDateTime.now();

        Loan loan = new Loan();
        loan.setSacco(member.getSacco());
        loan.setMember(member);
        loan.setLoanType(request.getLoanType());
        loan.setAmountApplied(request.getAmountApplied());
        loan.setRepaymentPeriodMonths(request.getRepaymentPeriodMonths());
        loan.setPurpose(request.getPurpose());
        loan.setAppliedAt(now);

        Loan saved = loanRepository.save(loan);

        StartWorkFlowRequest startRequest = new StartWorkFlowRequest();
        startRequest.setProcessTypeKey(PROCESS_TYPE_KEY);
        startRequest.setReferenceId(saved.getId());
        startRequest.setProcessName(request.getLoanType().name() + " Loan Application");
        startRequest.setPriority(WorkFlowPriority.MEDIUM);
        startRequest.setData(buildDataSnapshot(saved, member));

        WorkFlowInstanceResponse instance = workFlowEngineService.startProcess(startRequest, applicant);

        saved.setWorkflowInstanceId(instance.getId());
        Loan withInstance = loanRepository.save(saved);

        return toResponse(withInstance, instance);
    }

    public List<LoanResponse> listLoansForSacco(UUID saccoId) {
        return loanRepository.findBySaccoId(saccoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<LoanResponse> listMyLoans(User user) {
        Member member = memberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No member profile found for this user"));
        return loanRepository.findByMemberId(member.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LoanResponse getLoanById(UUID id) {
        return toResponse(findLoan(id));
    }

    private Loan findLoan(UUID id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
    }

    private Map<String, Object> buildDataSnapshot(Loan loan, Member member) {
        Map<String, Object> data = new HashMap<>();
        data.put("loanType", loan.getLoanType().name());
        data.put("amountApplied", loan.getAmountApplied());
        data.put("repaymentPeriodMonths", loan.getRepaymentPeriodMonths());
        data.put("purpose", loan.getPurpose());
        data.put("memberName", member.getFirstName() + " " + member.getLastName());
        data.put("memberNumber", member.getMemberNumber());
        return data;
    }

    private LoanResponse toResponse(Loan loan) {
        WorkFlowInstanceResponse instance = loan.getWorkflowInstanceId() != null
                ? workFlowInstanceService.getInstanceById(loan.getWorkflowInstanceId())
                : null;
        return toResponse(loan, instance);
    }

    private LoanResponse toResponse(Loan loan, WorkFlowInstanceResponse instance) {
        Member member = loan.getMember();
        return LoanResponse.builder()
                .id(loan.getId())
                .memberId(member.getId())
                .memberName(member.getFirstName() + " " + member.getLastName())
                .memberNumber(member.getMemberNumber())
                .loanType(loan.getLoanType())
                .amountApplied(loan.getAmountApplied())
                .amountApproved(loan.getAmountApproved())
                .interestRate(loan.getInterestRate())
                .repaymentPeriodMonths(loan.getRepaymentPeriodMonths())
                .purpose(loan.getPurpose())
                .disbursedAmount(loan.getDisbursedAmount())
                .disbursedAt(loan.getDisbursedAt())
                .appliedAt(loan.getAppliedAt())
                .workflowInstanceId(loan.getWorkflowInstanceId())
                .status(instance != null ? instance.getStatus() : null)
                .currentStageName(instance != null ? instance.getCurrentStageName() : null)
                .priority(instance != null ? instance.getPriority() : null)
                .build();
    }
}
