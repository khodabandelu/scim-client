package com.khodabandelu.scim.client.api.services;

import com.khodabandelu.scim.client.api.commands.LookupUserCommand;
import com.khodabandelu.scim.client.domains.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findById(String id);

    Page<User> findAll(LookupUserCommand command, Pageable pageable);

    void activate(String userId);
}
