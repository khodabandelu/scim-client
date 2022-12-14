package com.khodabandelu.scim.client.security.token;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

public class TokenFilter extends GenericFilterBean {

    private final TokenService tokenService;

    private static final Pattern provisionerPattern = Pattern.compile(".*/api/organizations/.*/provisioner/.*");

    public TokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            var token = bearerToken.substring(7);
            if (!provisionerPattern.matcher(httpServletRequest.getRequestURI()).matches()) {
                SecurityContextHolder.getContext().setAuthentication(tokenService.validateTokenAndRetrieve(token));
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
