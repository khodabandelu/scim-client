package com.khodabandelu.scim.client.dao;

import com.khodabandelu.scim.client.domains.provisioner.ProvisionerSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvisionerSecretRepository extends JpaRepository<ProvisionerSecret, String> {
    Optional<ProvisionerSecret> findByOrganizationIdAndProvisionerId(String organizationId, String provisionerId);

}
