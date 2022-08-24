package com.khodabandelu.scim.client.api.services;

import com.khodabandelu.scim.client.domains.user.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findById(String id);
}
