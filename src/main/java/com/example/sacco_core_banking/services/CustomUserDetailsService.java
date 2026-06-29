package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.AppUserDetails;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserRole;
import com.example.sacco_core_banking.repositories.UserRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No account found for email: " + email));

        List<Role> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList());

        return new AppUserDetails(user, roles);
    }
}
