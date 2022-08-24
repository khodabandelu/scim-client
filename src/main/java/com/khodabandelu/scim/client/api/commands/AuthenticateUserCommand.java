package com.khodabandelu.scim.client.api.commands;

import lombok.Data;

/**
 * This class use for get data as a command and use it for authenticate user flow.
 *
 * @author Mahdi Khodabandelu
 */
@Data
public class AuthenticateUserCommand {
    private String username;
    private String password;
}
