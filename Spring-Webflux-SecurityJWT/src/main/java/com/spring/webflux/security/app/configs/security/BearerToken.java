package com.spring.webflux.security.app.configs.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class BearerToken extends AbstractAuthenticationToken {

    final private String token;

    public BearerToken(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
    }

    @Override
    public String getCredentials() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return null;
    }
}
