package com.khodabandelu.scim.client.api.services.impl;

import com.khodabandelu.scim.client.api.commands.AuthenticateUserCommand;
import com.khodabandelu.scim.client.api.services.AccountService;
import com.khodabandelu.scim.client.security.token.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing accounts and authentication flow.
 *
 * @author Mahdi Khodabandelu
 */
@Service
public class AccountServiceImpl implements AccountService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenService tokenService;


    public AccountServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, TokenService tokenService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenService = tokenService;
    }

    /**
     * authenticate user.
     * first authenticate user and then return generated token.
     *
     * @param command the command of authentication needed.
     * @return generated jwt token .
     */
    @Override
    public String authenticate(AuthenticateUserCommand command) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(command.getUsername(), command.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenService.generateToken(authentication);
    }


}
