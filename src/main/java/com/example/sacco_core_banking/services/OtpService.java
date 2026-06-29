package com.example.sacco_core_banking.services;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.entities.OtpToken;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.enums.OtpPurpose;
import com.example.sacco_core_banking.repositories.OtpTokenRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generates, emails, and verifies one-time codes. A fresh send invalidates any prior
 * unused code for the same user+purpose, so only the most recently issued code works.
 */
@Service
@Transactional
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private OtpTokenRepository otpTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Value("${app.otp.length}")
    private int otpLength;

    @Value("${app.otp.expiration-minutes}")
    private int expirationMinutes;

    public void generateAndSendOtp(User user, OtpPurpose purpose) {
        otpTokenRepository.findTopByUserIdAndPurposeAndUsedFalseOrderByCreatedAtDesc(user.getId(), purpose)
                .ifPresent(existing -> {
                    existing.setUsed(true);
                    otpTokenRepository.save(existing);
                });

        String code = generateCode();

        OtpToken token = new OtpToken();
        token.setUser(user);
        token.setCode(code);
        token.setPurpose(purpose);
        token.setExpiresAt(OffsetDateTime.now().plusMinutes(expirationMinutes));
        otpTokenRepository.save(token);

        emailService.sendOtpEmail(user.getEmail(), user.getUsername(), code, expirationMinutes);
    }

    public void verifyOtp(String email, String code, OtpPurpose purpose) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for email: " + email));

        OtpToken token = otpTokenRepository
                .findTopByUserIdAndPurposeAndUsedFalseOrderByCreatedAtDesc(user.getId(), purpose)
                .orElseThrow(() -> new InvalidStateException("No verification code is pending for this account"));

        if (token.isUsed() || token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new InvalidStateException("This verification code has expired. Please request a new one.");
        }

        if (!token.getCode().equals(code)) {
            throw new InvalidStateException("Incorrect verification code");
        }

        token.setUsed(true);
        otpTokenRepository.save(token);

        if (purpose == OtpPurpose.REGISTRATION) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            code.append(RANDOM.nextInt(10));
        }
        return code.toString();
    }
}
