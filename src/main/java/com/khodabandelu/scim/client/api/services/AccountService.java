package com.khodabandelu.scim.client.api.services;

import com.khodabandelu.scim.client.api.commands.AuthenticateUserCommand;

public interface AccountService {
    String authenticate(AuthenticateUserCommand command);
}
