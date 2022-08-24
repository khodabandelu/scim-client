package com.khodabandelu.scim.client.api.commands;

import com.khodabandelu.scim.client.domains.provisioner.ProvisionerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class use for get data as a command and use it by admin to sync his organization with external identity.
 *
 * @author Mahdi Khodabandelu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProvisionerCommand extends BaseCommand {
    private String name;
    private ProvisionerType provisionerType;
}
