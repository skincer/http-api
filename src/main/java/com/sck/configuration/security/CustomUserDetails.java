package com.sck.configuration.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * Created by TEKKINCERS on 5/18/2017.
 */
public class CustomUserDetails implements UserDetails {

    private final int userId;
    private final String login;
    private final String password;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(int userId, String login, String password, Set<GrantedAuthority> authorities) {
        this.userId = userId;
        this.login = login;
        this.password = password;
        this.authorities = authorities;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
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
