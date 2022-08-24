package com.khodabandelu.scim.client.api.services;

import com.khodabandelu.scim.client.api.commands.ProvisionUserCommand;

public interface UserProvisionerService {
    String provisionUser(ProvisionUserCommand command,String organizationId,String provisionerId);

}
