package com.example.sacco_core_banking.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.member.MemberResponse;
import com.example.sacco_core_banking.entities.Member;
import com.example.sacco_core_banking.entities.MemberApproval;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.enums.ApprovalDecision;
import com.example.sacco_core_banking.enums.UserStatus;
import com.example.sacco_core_banking.repositories.MemberApprovalRepository;
import com.example.sacco_core_banking.repositories.MemberRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Approve/reject decisions on a Member's PENDING registration. Kept separate from
 * UserService.updateStatus, which only ever handles already-active accounts
 * (suspend/lock/deactivate/reactivate) — the PENDING -> ACTIVE/REJECTED transition only
 * happens here, and every decision is recorded as a MemberApproval row for audit trail.
 */
@Service
@Transactional
public class MemberApprovalService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberApprovalRepository memberApprovalRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private AuditLogService auditLogService;

    public List<MemberResponse> listPending(UUID saccoId) {
        return memberService.listPendingMembers(saccoId);
    }

    public MemberResponse approve(User admin, UUID memberId, String comments) {
        Member member = findPendingInSacco(memberId, admin.getSacco().getId());

        User user = member.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        if (member.getMemberNumber() == null || member.getMemberNumber().isBlank()) {
            long sequence = memberRepository.countBySaccoId(admin.getSacco().getId()) + 1;
            member.setMemberNumber(String.format("MEM-%06d", sequence));
            memberRepository.save(member);
        }

        recordDecision(member, admin, ApprovalDecision.APPROVED, comments);
        auditLogService.record(admin, "MEMBER_APPROVED", "Member", member.getId());

        return memberService.getMemberById(member.getId(), admin.getSacco().getId());
    }

    public MemberResponse reject(User admin, UUID memberId, String comments) {
        Member member = findPendingInSacco(memberId, admin.getSacco().getId());

        User user = member.getUser();
        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);

        recordDecision(member, admin, ApprovalDecision.REJECTED, comments);
        auditLogService.record(admin, "MEMBER_REJECTED", "Member", member.getId());

        return memberService.getMemberById(member.getId(), admin.getSacco().getId());
    }

    private void recordDecision(Member member, User admin, ApprovalDecision decision, String comments) {
        MemberApproval approval = new MemberApproval();
        approval.setMember(member);
        approval.setApprovedBy(admin);
        approval.setDecision(decision);
        approval.setComments(comments);
        approval.setDecidedAt(OffsetDateTime.now());
        memberApprovalRepository.save(approval);
    }

    private Member findPendingInSacco(UUID memberId, UUID saccoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!member.getSacco().getId().equals(saccoId)) {
            throw new ResourceNotFoundException("Member not found");
        }

        if (member.getUser().getStatus() != UserStatus.PENDING) {
            throw new InvalidStateException("Only pending registrations can be approved or rejected");
        }

        return member;
    }
}
