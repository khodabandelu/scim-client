package com.khodabandelu.scim.client.api.services.impl;

import com.khodabandelu.scim.client.api.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * custom user Details service to load .
 *
 * @author Mahdi Khodabandelu
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var lowercaseUsername = username.toLowerCase();

        return this.userService.findByEmail(lowercaseUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseUsername + " was not found in database"));
    }
}
