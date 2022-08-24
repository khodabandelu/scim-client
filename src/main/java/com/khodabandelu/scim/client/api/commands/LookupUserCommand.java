package com.khodabandelu.scim.client.api.commands;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class use for query users by parameters.
 *
 * for next practice we should use some practice to define how to filter data for example choice like or not like.
 * @author Mahdi Khodabandelu
 */
@Data
@NoArgsConstructor
public class LookupUserCommand extends BaseCommand {
    private String email;
    private Boolean active;
    private String organizationId;
    private String externalId;
}
