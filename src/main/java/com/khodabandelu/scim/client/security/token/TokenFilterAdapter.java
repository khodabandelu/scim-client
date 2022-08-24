package com.khodabandelu.scim.client.security.token;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class TokenFilterAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenService tokenService;

    public TokenFilterAdapter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(new TokenFilter(tokenService), UsernamePasswordAuthenticationFilter.class);
    }

}
