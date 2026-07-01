package com.example.sacco_core_banking.services;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.entities.ActivationToken;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.enums.UserStatus;
import com.example.sacco_core_banking.repositories.ActivationTokenRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivationTokenService {

    private static final int EXPIRY_HOURS = 24;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;
    @Autowired
    private UserRepository userRepository;

    public ActivationToken createToken(User user) {
        activationTokenRepository.findByUserIdAndUsedFalse(user.getId())
                .forEach(t -> {
                    t.setUsed(true);
                    activationTokenRepository.save(t);
                });

        ActivationToken token = new ActivationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(OffsetDateTime.now().plusHours(EXPIRY_HOURS));
        return activationTokenRepository.save(token);
    }

    public User validateAndConsume(String tokenValue) {
        ActivationToken token = activationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired activation link"));

        if (token.isUsed()) {
            throw new InvalidStateException("This activation link has already been used");
        }
        if (token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new InvalidStateException("This activation link has expired. Please contact your administrator for a new invitation.");
        }

        token.setUsed(true);
        activationTokenRepository.save(token);

        User user = token.getUser();
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }
}
