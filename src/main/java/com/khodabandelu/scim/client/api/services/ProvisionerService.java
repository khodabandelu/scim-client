package com.khodabandelu.scim.client.api.services;

import com.khodabandelu.scim.client.api.commands.CreateProvisionerCommand;
import com.khodabandelu.scim.client.api.commands.SyncProvisioningCommand;
import com.khodabandelu.scim.client.domains.provisioner.Provisioner;
import com.khodabandelu.scim.client.domains.provisioner.ProvisionerSecret;

import java.util.Optional;

public interface ProvisionerService {

    Provisioner save(CreateProvisionerCommand command);

    String syncProvisioning(SyncProvisioningCommand command);

    Optional<ProvisionerSecret> findByOrganizationIdAndProvisionerId(String organizationID, String provisionerId);
}
