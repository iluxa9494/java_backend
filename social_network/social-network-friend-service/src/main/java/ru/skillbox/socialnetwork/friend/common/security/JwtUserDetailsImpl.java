package ru.skillbox.socialnetwork.friend.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skillbox.socialnetwork.friend.common.security.jwt.UserJwtPayload;

import java.util.Collection;
import java.util.UUID;

public class JwtUserDetailsImpl implements UserDetails {

    private final UUID id;

    private String token;

    public JwtUserDetailsImpl(UUID id, String token) {
        this.id = id;
        this.token = token;
    }

    public JwtUserDetailsImpl(UUID id) {
        this.id = id;
    }

    public static JwtUserDetailsImpl build(UserJwtPayload user) {
        return new JwtUserDetailsImpl(user.getId());
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}