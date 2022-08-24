package com.khodabandelu.scim.client.api.commands;

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
public class SyncProvisioningCommand extends BaseCommand {
    private String provisionerId;
}
