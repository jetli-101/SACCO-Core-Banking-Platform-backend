package com.example.sacco_core_banking.seeders;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import com.example.sacco_core_banking.entities.Member;
import com.example.sacco_core_banking.entities.MemberApproval;
import com.example.sacco_core_banking.entities.ModuleRegister;
import com.example.sacco_core_banking.entities.ModuleType;
import com.example.sacco_core_banking.entities.NextOfKin;
import com.example.sacco_core_banking.entities.Permission;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.Sacco;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserModulePermission;
import com.example.sacco_core_banking.entities.UserRole;
import com.example.sacco_core_banking.enums.ApprovalDecision;
import com.example.sacco_core_banking.enums.Gender;
import com.example.sacco_core_banking.enums.RoleName;
import com.example.sacco_core_banking.enums.SaccoStatus;
import com.example.sacco_core_banking.enums.UserStatus;
import com.example.sacco_core_banking.repositories.MemberApprovalRepository;
import com.example.sacco_core_banking.repositories.MemberRepository;
import com.example.sacco_core_banking.repositories.ModuleRegisterRepository;
import com.example.sacco_core_banking.repositories.ModuleTypeRepository;
import com.example.sacco_core_banking.repositories.NextOfKinRepository;
import com.example.sacco_core_banking.repositories.PermissionRepository;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.SaccoRepository;
import com.example.sacco_core_banking.repositories.UserModulePermissionRepository;
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
    private final ModuleTypeRepository moduleTypeRepository;
    private final ModuleRegisterRepository moduleRegisterRepository;
    private final UserModulePermissionRepository userModulePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String memberPassword;

    public DataSeeder(SaccoRepository saccoRepository, RoleRepository roleRepository, UserRepository userRepository,
            UserRoleRepository userRoleRepository, MemberRepository memberRepository,
            NextOfKinRepository nextOfKinRepository, MemberApprovalRepository memberApprovalRepository,
            ModuleTypeRepository moduleTypeRepository, ModuleRegisterRepository moduleRegisterRepository,
            UserModulePermissionRepository userModulePermissionRepository, PermissionRepository permissionRepository,
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
        this.moduleTypeRepository = moduleTypeRepository;
        this.moduleRegisterRepository = moduleRegisterRepository;
        this.userModulePermissionRepository = userModulePermissionRepository;
        this.permissionRepository = permissionRepository;
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
        seedMember(sacco, admin, "Wanjiku", null, "Mwangi", "wanjiku.mwangi@smoothsurfsacco.co.ke",
                "0712345671", "28456712", LocalDate.of(1988, 3, 14), Gender.FEMALE, "Teacher",
                "A001234567B", "Kiambu Primary School", "Kiambu", "Kiambu", "Township",
                "P.O. Box 112, Kiambu", "Township Estate, Kiambu Town", "SS-0001",
                "Mwangi Kamau", "Spouse", "0712345699", "29456712", "Township Estate, Kiambu Town");
        seedMember(sacco, admin, "Otieno", null, "Odhiambo", "otieno.odhiambo@smoothsurfsacco.co.ke",
                "0722345672", "27345689", LocalDate.of(1991, 7, 22), Gender.MALE, "Boda Boda Operator",
                "A002345678C", "Self-employed", "Kisumu", "Kisumu Central", "Market Milimani",
                "P.O. Box 884, Kisumu", "Milimani Estate, Kisumu", "SS-0002",
                "Achieng Odhiambo", "Sister", "0722345698", "26345689", "Milimani Estate, Kisumu");
        seedMember(sacco, admin, "Amina", null, "Hassan", "amina.hassan@smoothsurfsacco.co.ke",
                "0733345673", "26234567", LocalDate.of(1995, 11, 2), Gender.FEMALE, "Trader",
                "A003456789D", "Self-employed", "Mombasa", "Mvita", "Tudor",
                "P.O. Box 552, Mombasa", "Tudor Estate, Mombasa", "SS-0003",
                "Hassan Ali", "Father", "0733345697", "20234567", "Tudor Estate, Mombasa");
        seedMember(sacco, admin, "Kiprotich", null, "Kiplagat", "kiprotich.kiplagat@smoothsurfsacco.co.ke",
                "0744345674", "25123456", LocalDate.of(1985, 5, 30), Gender.MALE, "Farmer",
                "A004567890E", "Self-employed", "Uasin Gishu", "Ainabkoi", "Kapsoya",
                "P.O. Box 220, Eldoret", "Kapsoya Estate, Eldoret", "SS-0004",
                "Chebet Kiplagat", "Spouse", "0744345696", "24123456", "Kapsoya Estate, Eldoret");

        User teller = seedStaff(sacco, "teller.achieng@smoothsurfsacco.co.ke", "0755345675", RoleName.ROLE_TELLER);

        seedModules(teller);
    }

    private void seedModules(User teller) {
        ModuleType membership = seedModuleType("Membership", "membership", 1);
        ModuleType administration = seedModuleType("Administration", "administration", 2);

        ModuleRegister memberDirectory = seedModule("Member Directory", "member-directory",
                "/dashboard/members", "icon-users", 1, membership);
        ModuleRegister pendingApprovals = seedModule("Pending Approvals", "pending-approvals",
                "/dashboard/admin/approvals", "icon-clock", 2, membership);
        ModuleRegister userManagement = seedModule("User Management", "user-management",
                "/dashboard/admin/users", "icon-settings", 1, administration);
        ModuleRegister rolesAndPermissions = seedModule("Roles & Permissions", "roles-permissions",
                "/dashboard/admin/roles", "icon-shield", 2, administration);

        Role admin = roleRepository.findByName(RoleName.ROLE_SYSTEM_ADMINISTRATOR.name())
                .orElseThrow(() -> new IllegalStateException("Roles have not been seeded yet"));
        Role branchManager = roleRepository.findByName(RoleName.ROLE_BRANCH_MANAGER.name())
                .orElseThrow(() -> new IllegalStateException("Roles have not been seeded yet"));
        Role tellerRole = roleRepository.findByName(RoleName.ROLE_TELLER.name())
                .orElseThrow(() -> new IllegalStateException("Roles have not been seeded yet"));

        grantModules(admin, memberDirectory, pendingApprovals, userManagement, rolesAndPermissions);
        grantModules(branchManager, memberDirectory, pendingApprovals);
        grantModules(tellerRole, memberDirectory);

        grantUserPermission(teller, pendingApprovals, "READ", "APPROVE");
    }

    private ModuleType seedModuleType(String name, String textId, int orderNo) {
        return moduleTypeRepository.findByTextId(textId).orElseGet(() -> {
            ModuleType moduleType = new ModuleType();
            moduleType.setName(name);
            moduleType.setTextId(textId);
            moduleType.setOrderNo(orderNo);
            return moduleTypeRepository.save(moduleType);
        });
    }

    private ModuleRegister seedModule(String name, String textId, String urlPath, String icon, int orderNo,
            ModuleType moduleType) {
        return moduleRegisterRepository.findByTextId(textId).orElseGet(() -> {
            ModuleRegister module = new ModuleRegister();
            module.setName(name);
            module.setTextId(textId);
            module.setUrlPath(urlPath);
            module.setIcon(icon);
            module.setOrderNo(orderNo);
            module.setModuleType(moduleType);
            return moduleRegisterRepository.save(module);
        });
    }

    private void grantModules(Role role, ModuleRegister... modules) {
        Set<ModuleRegister> granted = new HashSet<>(role.getModules());
        for (ModuleRegister module : modules) {
            granted.add(module);
        }
        role.setModules(granted);
        roleRepository.save(role);
    }

    private void grantUserPermission(User user, ModuleRegister module, String... permissionNames) {
        Set<Permission> permissions = new HashSet<>();
        for (String name : permissionNames) {
            permissions.add(permissionRepository.findByName(name)
                    .orElseThrow(() -> new IllegalStateException("Permissions have not been seeded yet")));
        }

        UserModulePermission permission = userModulePermissionRepository
                .findByUserIdAndModuleRegisterId(user.getId(), module.getId())
                .orElseGet(() -> {
                    UserModulePermission created = new UserModulePermission();
                    created.setUser(user);
                    created.setModuleRegister(module);
                    return created;
                });
        permission.setPermissions(permissions);
        userModulePermissionRepository.save(permission);
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

    private void seedMember(Sacco sacco, User admin, String firstName, String middleName, String lastName,
            String email, String phone, String nationalId, LocalDate dateOfBirth, Gender gender, String occupation,
            String kraPin, String employer, String county, String constituency, String ward, String postalAddress,
            String physicalAddress, String memberNumber, String kinName, String kinRelationship, String kinPhone,
            String kinIdNumber, String kinAddress) {

        if (userRepository.existsByEmail(email)) {
            return;
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
        approval.setApprovedBy(admin);
        approval.setDecision(ApprovalDecision.APPROVED);
        approval.setComments("Seeded demo member — auto-approved");
        approval.setDecidedAt(OffsetDateTime.now());
        memberApprovalRepository.save(approval);
    }
}
