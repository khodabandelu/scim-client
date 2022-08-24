package com.khodabandelu.scim.client.api.services.impl;

import com.khodabandelu.scim.client.api.services.UserService;
import com.khodabandelu.scim.client.dao.UserRepository;
import com.khodabandelu.scim.client.domains.user.User;
import org.springframework.stereotype.Service;

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


}
