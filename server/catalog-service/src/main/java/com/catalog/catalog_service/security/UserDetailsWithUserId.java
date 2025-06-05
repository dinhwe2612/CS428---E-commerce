package com.catalog.catalog_service.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserDetailsWithUserId extends User {
    
    private final String userId;
    private final String role;

    public UserDetailsWithUserId(String username, String password, Collection<? extends GrantedAuthority> authorities, String userId, String role) {
        super(username, password, authorities);
        this.userId = userId;
        this.role = role;
    }

    public UserDetailsWithUserId(String username, String password, boolean enabled, boolean accountNonExpired,
                               boolean credentialsNonExpired, boolean accountNonLocked,
                               Collection<? extends GrantedAuthority> authorities, String userId, String role) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.role = role;
    }
} 