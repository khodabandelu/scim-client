package com.khodabandelu.scim.client.api.services.impl;

import com.khodabandelu.scim.client.api.commands.LookupUserCommand;
import com.khodabandelu.scim.client.api.services.UserService;
import com.khodabandelu.scim.client.dao.UserRepository;
import com.khodabandelu.scim.client.domains.user.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return this.userRepository.findById(id);
    }

    @Override
    public Page<User> findAll(LookupUserCommand command, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        var userLookup = User.builder()
                .active(command.getActive())
                .organizationId(command.getOrganizationId())
                .email(command.getEmail())
                .externalId(command.getExternalId())
                .build();
        return this.userRepository.findAll(Example.of(userLookup, matcher), pageable);
    }

    @Transactional
    @Override
    public void activate(String userId) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        var userLogin = (User) securityContext.getAuthentication().getPrincipal();
        this.userRepository.findByIdAndOrganizationId(userId, userLogin.getOrganizationId()).ifPresentOrElse(user -> user.setActive(true),
                () -> {
                    throw new IllegalStateException("This user is not found!");
                });
    }


}
