package com.fyp.dhumbal.global.config.security;

import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomAuthentication implements Authentication {

    private final String tokenId;
    private final String name;
    private final String refreshTokenId;
    private final String userId;
    private final UserType userType;
    private final String email;
    private final List<SimpleGrantedAuthority> permissions;
    private boolean authenticated = true;

    public CustomAuthentication(String tokenId, String refreshTokenId, UserEntity userEntity) {
        this.tokenId = tokenId;
        this.refreshTokenId = refreshTokenId;
        this.userId = userEntity.getId();
        this.email = userEntity.getEmail();
        this.name = userEntity.getName();
        this.userType = userEntity.getUserType();
        this.permissions = Collections.singletonList(new SimpleGrantedAuthority(userEntity.getUserType().name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.permissions;
    }

    @Override
    public Object getCredentials() {
        return this.email;
    }

    @Override
    public Object getDetails() {
        return this.userId;
    }

    public String getUserId() {
        return this.userId;
    }

    @Override
    public Object getPrincipal() {
        return this.email;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getRefreshTokenId() {
        return refreshTokenId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public UserType getUserType() {
        return userType;
    }
}
