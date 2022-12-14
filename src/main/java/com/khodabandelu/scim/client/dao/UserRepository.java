package com.khodabandelu.scim.client.dao;

import com.khodabandelu.scim.client.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndOrganizationId(String id, String organizationId);
}
