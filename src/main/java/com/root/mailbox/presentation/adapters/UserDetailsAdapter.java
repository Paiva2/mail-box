package com.root.mailbox.presentation.adapters;

import com.root.mailbox.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Builder
public class UserDetailsAdapter implements UserDetails {
    private Long id;
    private String password;
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role.equals(Role.ADMIN)) {
            List<GrantedAuthority> adminAuths = new ArrayList<>();

            for (Role role : Role.values()) {
                adminAuths.add(new SimpleGrantedAuthority("ROLE_".concat(role.name())));
            }

            return adminAuths;
        }

        return List.of(new SimpleGrantedAuthority("ROLE_".concat(this.role.toString())));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
