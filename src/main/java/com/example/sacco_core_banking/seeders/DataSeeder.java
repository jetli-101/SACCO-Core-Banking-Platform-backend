package com.example.sacco_core_banking.seeders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import com.example.sacco_core_banking.classes.LoanAmortizationCalculator;
import com.example.sacco_core_banking.entities.Guarantor;
import com.example.sacco_core_banking.entities.Loan;
import com.example.sacco_core_banking.entities.LoanProduct;
import com.example.sacco_core_banking.entities.Member;
import com.example.sacco_core_banking.entities.MemberApproval;
import com.example.sacco_core_banking.entities.NextOfKin;
import com.example.sacco_core_banking.entities.Repayment;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.Sacco;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserRole;
import com.example.sacco_core_banking.enums.ApprovalDecision;
import com.example.sacco_core_banking.enums.Gender;
import com.example.sacco_core_banking.enums.GuarantorStatus;
import com.example.sacco_core_banking.enums.LoanType;
import com.example.sacco_core_banking.enums.RoleName;
import com.example.sacco_core_banking.enums.SaccoStatus;
import com.example.sacco_core_banking.enums.UserStatus;
import com.example.sacco_core_banking.repositories.GuarantorRepository;
import com.example.sacco_core_banking.repositories.LoanProductRepository;
import com.example.sacco_core_banking.repositories.LoanRepository;
import com.example.sacco_core_banking.repositories.MemberApprovalRepository;
import com.example.sacco_core_banking.repositories.MemberRepository;
import com.example.sacco_core_banking.repositories.NextOfKinRepository;
import com.example.sacco_core_banking.repositories.RepaymentRepository;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.SaccoRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Demo data for local/staging environments: one Kenyan Sacco, an admin account, and a
 * handful of approved members spanning different counties so dashboards/lists have
 * something realistic to show. Disabled by setting app.seed.enabled=false (e.g. in prod).
 * Runs after RoleSeeder/PermissionSeeder (Order 2) since it needs both to already exist.
 */
@Component
@Order(2)
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private final SaccoRepository saccoRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final MemberRepository memberRepository;
    private final NextOfKinRepository nextOfKinRepository;
    private final MemberApprovalRepository memberApprovalRepository;
    private final LoanRepository loanRepository;
    private final LoanProductRepository loanProductRepository;
    private final GuarantorRepository guarantorRepository;
    private final RepaymentRepository repaymentRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String memberPassword;

    public DataSeeder(SaccoRepository saccoRepository, RoleRepository roleRepository, UserRepository userRepository,
            UserRoleRepository userRoleRepository, MemberRepository memberRepository,
            NextOfKinRepository nextOfKinRepository, MemberApprovalRepository memberApprovalRepository,
            LoanRepository loanRepository, LoanProductRepository loanProductRepository,
            GuarantorRepository guarantorRepository, RepaymentRepository repaymentRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.admin-password}") String adminPassword,
            @Value("${app.seed.member-password}") String memberPassword) {
        this.saccoRepository = saccoRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.memberRepository = memberRepository;
        this.nextOfKinRepository = nextOfKinRepository;
        this.memberApprovalRepository = memberApprovalRepository;
        this.loanRepository = loanRepository;
        this.loanProductRepository = loanProductRepository;
        this.guarantorRepository = guarantorRepository;
        this.repaymentRepository = repaymentRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.memberPassword = memberPassword;
    }

    private static final String SACCO_CODE = "SASRA/SACCO/0001";

    @Override
    @Transactional
    public void run(String... args) {
        Sacco sacco = saccoRepository.findByRegistrationNumber(SACCO_CODE).orElseGet(this::seedSacco);
        User admin = seedAdmin(sacco);
        seedAdminMemberProfile(sacco, admin);
        Member wanjiku = seedMember(sacco, admin, "Wanjiku", null, "Mwangi", "wanjiku.mwangi@smoothsurfsacco.co.ke",
                "0712345671", "28456712", LocalDate.of(1988, 3, 14), Gender.FEMALE, "Teacher",
                "A001234567B", "Kiambu Primary School", "Kiambu", "Kiambu", "Township",
                "P.O. Box 112, Kiambu", "Township Estate, Kiambu Town", "SS-0001",
                "Mwangi Kamau", "Spouse", "0712345699", "29456712", "Township Estate, Kiambu Town");
        Member otieno = seedMember(sacco, admin, "Otieno", null, "Odhiambo", "otieno.odhiambo@smoothsurfsacco.co.ke",
                "0722345672", "27345689", LocalDate.of(1991, 7, 22), Gender.MALE, "Boda Boda Operator",
                "A002345678C", "Self-employed", "Kisumu", "Kisumu Central", "Market Milimani",
                "P.O. Box 884, Kisumu", "Milimani Estate, Kisumu", "SS-0002",
                "Achieng Odhiambo", "Sister", "0722345698", "26345689", "Milimani Estate, Kisumu");
        Member amina = seedMember(sacco, admin, "Amina", null, "Hassan", "amina.hassan@smoothsurfsacco.co.ke",
                "0733345673", "26234567", LocalDate.of(1995, 11, 2), Gender.FEMALE, "Trader",
                "A003456789D", "Self-employed", "Mombasa", "Mvita", "Tudor",
                "P.O. Box 552, Mombasa", "Tudor Estate, Mombasa", "SS-0003",
                "Hassan Ali", "Father", "0733345697", "20234567", "Tudor Estate, Mombasa");
        Member kiprotich = seedMember(sacco, admin, "Kiprotich", null, "Kiplagat", "kiprotich.kiplagat@smoothsurfsacco.co.ke",
                "0744345674", "25123456", LocalDate.of(1985, 5, 30), Gender.MALE, "Farmer",
                "A004567890E", "Self-employed", "Uasin Gishu", "Ainabkoi", "Kapsoya",
                "P.O. Box 220, Eldoret", "Kapsoya Estate, Eldoret", "SS-0004",
                "Chebet Kiplagat", "Spouse", "0744345696", "24123456", "Kapsoya Estate, Eldoret");

        seedStaff(sacco, "teller.achieng@smoothsurfsacco.co.ke", "0755345675", RoleName.ROLE_TELLER);
        seedStaff(sacco, "loanofficer.mutua@smoothsurfsacco.co.ke", "0766345676", RoleName.ROLE_LOAN_OFFICER);

        seedLoanProducts();
        seedLoansAndRepayments(sacco, wanjiku, otieno, amina, kiprotich);
    }

    private void assignRole(User user, Role role) {
        if (userRoleRepository.findByUserIdAndRoleId(user.getId(), role.getId()).isEmpty()) {
            userRoleRepository.save(new UserRole(user, role));
        }
    }

    private User seedStaff(Sacco sacco, String email, String phone, RoleName roleName) {
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User created = new User();
            created.setSacco(sacco);
            created.setUsername(email.substring(0, email.indexOf('@')));
            created.setEmail(email);
            created.setPhone(phone);
            created.setPassword(passwordEncoder.encode(memberPassword));
            created.setStatus(UserStatus.ACTIVE);
            return userRepository.save(created);
        });

        Role role = roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new IllegalStateException("Roles have not been seeded yet"));
        assignRole(user, role);

        return user;
    }

    private Sacco seedSacco() {
        Sacco sacco = new Sacco();
        sacco.setName("SmoothSurf Sacco");
        sacco.setRegistrationNumber(SACCO_CODE);
        sacco.setEmail("info@smoothsurfsacco.co.ke");
        sacco.setStatus(SaccoStatus.ACTIVE);
        return saccoRepository.save(sacco);
    }

    private User seedAdmin(Sacco sacco) {
        User user = userRepository.findByEmail("admin@smoothsurfsacco.co.ke").orElseGet(() -> {
            User created = new User();
            created.setSacco(sacco);
            created.setUsername("admin");
            created.setEmail("admin@smoothsurfsacco.co.ke");
            created.setPhone("0700000001");
            created.setPassword(passwordEncoder.encode(adminPassword));
            created.setStatus(UserStatus.ACTIVE);
            return userRepository.save(created);
        });

        Role adminRole = roleRepository.findByName(RoleName.ROLE_SYSTEM_ADMINISTRATOR.name())
                .orElseThrow(() -> new IllegalStateException("Roles have not been seeded yet"));
        assignRole(user, adminRole);

        return user;
    }

    private Member seedMember(Sacco sacco, User admin, String firstName, String middleName, String lastName,
            String email, String phone, String nationalId, LocalDate dateOfBirth, Gender gender, String occupation,
            String kraPin, String employer, String county, String constituency, String ward, String postalAddress,
            String physicalAddress, String memberNumber, String kinName, String kinRelationship, String kinPhone,
            String kinIdNumber, String kinAddress) {

        if (userRepository.existsByEmail(email)) {
            User existing = userRepository.findByEmail(email).orElseThrow();
            return memberRepository.findByUserId(existing.getId()).orElseThrow();
        }

        Role memberRole = roleRepository.findByName(RoleName.ROLE_MEMBER.name())
                .orElseThrow(() -> new IllegalStateException("Roles have not been seeded yet"));

        User user = new User();
        user.setSacco(sacco);
        user.setUsername(email.substring(0, email.indexOf('@')));
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(memberPassword));
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        assignRole(user, memberRole);

        return createMemberProfile(sacco, user, admin, firstName, middleName, lastName, nationalId, dateOfBirth, gender,
                occupation, kraPin, employer, county, constituency, ward, postalAddress, physicalAddress,
                memberNumber, kinName, kinRelationship, kinPhone, kinIdNumber, kinAddress);
    }

    /** Attaches a Member profile (+ next of kin + auto-approval) to a User that already exists, so a staff account like admin can also act as a member (e.g. apply for a loan) without a second login. */
    private void seedAdminMemberProfile(Sacco sacco, User admin) {
        if (memberRepository.findByUserId(admin.getId()).isPresent()) {
            return;
        }

        Role memberRole = roleRepository.findByName(RoleName.ROLE_MEMBER.name())
                .orElseThrow(() -> new IllegalStateException("Roles have not been seeded yet"));
        assignRole(admin, memberRole);

        createMemberProfile(sacco, admin, admin, "System", null, "Administrator", "A000000000Z",
                LocalDate.of(1990, 1, 1), Gender.MALE, "System Administrator", "A000000000Z",
                "SmoothSurf Sacco", "Nairobi", "Nairobi Central", "CBD",
                "P.O. Box 1, Nairobi", "CBD, Nairobi", "SS-0000",
                "N/A", "N/A", "0700000000", "00000000", "N/A");
    }

    private Member createMemberProfile(Sacco sacco, User user, User approver, String firstName, String middleName,
            String lastName, String nationalId, LocalDate dateOfBirth, Gender gender, String occupation,
            String kraPin, String employer, String county, String constituency, String ward, String postalAddress,
            String physicalAddress, String memberNumber, String kinName, String kinRelationship, String kinPhone,
            String kinIdNumber, String kinAddress) {

        Member member = new Member();
        member.setSacco(sacco);
        member.setUser(user);
        member.setMemberNumber(memberNumber);
        member.setNationalId(nationalId);
        member.setFirstName(firstName);
        member.setMiddleName(middleName);
        member.setLastName(lastName);
        member.setDateOfBirth(dateOfBirth);
        member.setGender(gender);
        member.setOccupation(occupation);
        member.setKraPin(kraPin);
        member.setEmployer(employer);
        member.setCounty(county);
        member.setConstituency(constituency);
        member.setWard(ward);
        member.setPostalAddress(postalAddress);
        member.setPhysicalAddress(physicalAddress);
        member = memberRepository.save(member);

        NextOfKin nextOfKin = new NextOfKin();
        nextOfKin.setMember(member);
        nextOfKin.setName(kinName);
        nextOfKin.setRelationship(kinRelationship);
        nextOfKin.setPhone(kinPhone);
        nextOfKin.setIdNumber(kinIdNumber);
        nextOfKin.setAddress(kinAddress);
        nextOfKinRepository.save(nextOfKin);

        MemberApproval approval = new MemberApproval();
        approval.setMember(member);
        approval.setApprovedBy(approver);
        approval.setDecision(ApprovalDecision.APPROVED);
        approval.setComments("Seeded demo member — auto-approved");
        approval.setDecidedAt(OffsetDateTime.now());
        memberApprovalRepository.save(approval);

        return member;
    }

    private void seedLoanProducts() {
        seedLoanProduct(LoanType.NORMAL, "Normal Loan", "General-purpose loan for any personal need.",
                new BigDecimal("12.00"), new BigDecimal("5000"), new BigDecimal("500000"), 3, 36);
        seedLoanProduct(LoanType.DEVELOPMENT, "Development Loan", "For long-term development projects like building or land purchase.",
                new BigDecimal("14.00"), new BigDecimal("20000"), new BigDecimal("2000000"), 6, 60);
        seedLoanProduct(LoanType.EMERGENCY, "Emergency Loan", "Fast-tracked loan for urgent, unplanned expenses.",
                new BigDecimal("16.00"), new BigDecimal("2000"), new BigDecimal("100000"), 1, 12);
        seedLoanProduct(LoanType.SCHOOL_FEES, "School Fees Loan", "Covers tuition and other school-related expenses.",
                new BigDecimal("10.00"), new BigDecimal("5000"), new BigDecimal("300000"), 1, 12);
    }

    private void seedLoanProduct(LoanType type, String name, String description, BigDecimal rate, BigDecimal minAmount,
            BigDecimal maxAmount, int minTerm, int maxTerm) {
        if (loanProductRepository.findByLoanType(type).isPresent()) {
            return;
        }
        LoanProduct product = new LoanProduct();
        product.setLoanType(type);
        product.setName(name);
        product.setDescription(description);
        product.setInterestRate(rate);
        product.setMinAmount(minAmount);
        product.setMaxAmount(maxAmount);
        product.setMinTermMonths(minTerm);
        product.setMaxTermMonths(maxTerm);
        product.setActive(true);
        loanProductRepository.save(product);
    }

    /** Seeds a spread of loans across lifecycle states (pending/active/defaulted/closed) plus
     * a guarantor invitation, so the Loans dashboards/analytics have real numbers to show
     * instead of being empty. Skipped if any loan already exists, so re-running the seeder
     * (e.g. after a restart) doesn't duplicate demo data. */
    private void seedLoansAndRepayments(Sacco sacco, Member wanjiku, Member otieno, Member amina, Member kiprotich) {
        if (loanRepository.count() > 0) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();

        seedLoan(sacco, wanjiku, LoanType.NORMAL, new BigDecimal("50000"), 12, new BigDecimal("12.00"),
                "Buying a delivery motorbike", now.minusDays(60), now.minusDays(55), new BigDecimal("50000"), 2);

        seedLoan(sacco, wanjiku, LoanType.EMERGENCY, new BigDecimal("20000"), 6, new BigDecimal("16.00"),
                "Medical emergency", now.minusDays(5), null, null, 0);

        seedLoan(sacco, otieno, LoanType.DEVELOPMENT, new BigDecimal("150000"), 24, new BigDecimal("14.00"),
                "Building a rental unit", now.minusDays(200), now.minusDays(190), new BigDecimal("150000"), 2);

        seedLoan(sacco, otieno, LoanType.SCHOOL_FEES, new BigDecimal("30000"), 6, new BigDecimal("10.00"),
                "Children's school fees", now.minusDays(190), now.minusDays(185), new BigDecimal("30000"), 6);

        seedLoan(sacco, amina, LoanType.NORMAL, new BigDecimal("80000"), 18, new BigDecimal("12.00"),
                "Stocking her retail shop", now.minusDays(45), now.minusDays(40), new BigDecimal("80000"), 1);

        seedLoan(sacco, amina, LoanType.SCHOOL_FEES, new BigDecimal("15000"), 3, new BigDecimal("10.00"),
                "Short course fees", now.minusDays(2), null, null, 0);

        Loan kiprotichLoan = seedLoan(sacco, kiprotich, LoanType.DEVELOPMENT, new BigDecimal("200000"), 36, new BigDecimal("14.00"),
                "Building a second dairy cow shed", now.minusDays(300), now.minusDays(290), new BigDecimal("200000"), 9);

        // Otieno accepted his guarantor invitation; Amina's is still pending her response.
        seedGuarantor(kiprotichLoan, otieno, new BigDecimal("50000"), GuarantorStatus.ACCEPTED, now.minusDays(288));
        seedGuarantor(kiprotichLoan, amina, new BigDecimal("50000"), GuarantorStatus.PENDING, null);
    }

    private Loan seedLoan(Sacco sacco, Member member, LoanType type, BigDecimal amount, int termMonths, BigDecimal rate,
            String purpose, OffsetDateTime appliedAt, OffsetDateTime disbursedAt, BigDecimal disbursedAmount, int installmentsPaid) {

        Loan loan = new Loan();
        loan.setSacco(sacco);
        loan.setMember(member);
        loan.setLoanType(type);
        loan.setAmountApplied(amount);
        loan.setRepaymentPeriodMonths(termMonths);
        loan.setPurpose(purpose);
        loan.setAppliedAt(appliedAt);
        if (disbursedAt != null) {
            loan.setAmountApproved(amount);
            loan.setInterestRate(rate);
            loan.setDisbursedAmount(disbursedAmount);
            loan.setDisbursedAt(disbursedAt);
        }
        Loan saved = loanRepository.save(loan);

        if (disbursedAt != null && installmentsPaid > 0) {
            List<LoanAmortizationCalculator.ScheduleRow> schedule = LoanAmortizationCalculator.buildSchedule(amount, rate, termMonths, disbursedAt);
            for (int i = 0; i < installmentsPaid && i < schedule.size(); i++) {
                LoanAmortizationCalculator.ScheduleRow row = schedule.get(i);
                Repayment repayment = new Repayment();
                repayment.setLoan(saved);
                repayment.setAmount(row.installment());
                repayment.setPaidAt(row.dueDate().minusDays(2));
                repayment.setMethod("MPESA");
                repaymentRepository.save(repayment);
            }
        }

        return saved;
    }

    private void seedGuarantor(Loan loan, Member guarantorMember, BigDecimal guaranteedAmount, GuarantorStatus status, OffsetDateTime respondedAt) {
        Guarantor guarantor = new Guarantor();
        guarantor.setLoan(loan);
        guarantor.setMember(guarantorMember);
        guarantor.setGuaranteedAmount(guaranteedAmount);
        guarantor.setStatus(status);
        guarantor.setRespondedAt(respondedAt);
        guarantorRepository.save(guarantor);
    }
}
