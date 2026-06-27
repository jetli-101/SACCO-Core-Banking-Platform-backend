package com.example.sacco_core_banking.classes;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Adapts our User entity to Spring Security's UserDetails contract. enabled/locked/
 * expired all delegate to User's own status-derived methods, so there is exactly one
 * place (User) that decides whether an account can authenticate. Roles are resolved
 * by the caller (via UserRoleRepository) and passed in, since User no longer holds
 * them directly — membership lives on the explicit UserRole entity.
 */
public class AppUserDetails implements UserDetails {

    private final User user;
    private final List<Role> roles;

    public AppUserDetails(User user, List<Role> roles) {
        this.user = user;
        this.roles = roles;
    }

    public User getUser() {
        return user;
    }

    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
