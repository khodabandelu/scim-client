package com.khodabandelu.scim.client.dao;

import com.khodabandelu.scim.client.domains.provisioner.Provisioner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvisionerRepository extends JpaRepository<Provisioner, String> {
}
