package com.khodabandelu.scim.client;

import com.khodabandelu.scim.client.domains.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser withUser) {
        Set<GrantedAuthority> authorities = Arrays.stream(withUser.roles())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User principal = User.builder().email(withUser.username()).organizationId(withUser.organizationId()).build();
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(principal, withUser.password(), authorities);
        context.setAuthentication(auth);
        return context;
    }
}