package com.khodabandelu.scim.client.api.commands;

import com.khodabandelu.scim.client.domains.user.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * This class use for get data as a command and use it for provisioning user from external identity.
 * this is response body that external identity expect it.
 *
 * @author Mahdi Khodabandelu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProvisionUserCommand extends BaseCommand {
    private String email;
    private Name name;
}
