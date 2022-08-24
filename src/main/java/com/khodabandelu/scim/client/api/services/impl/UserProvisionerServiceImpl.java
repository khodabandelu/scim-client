package com.khodabandelu.scim.client.api.services.impl;

import com.khodabandelu.scim.client.api.commands.ProvisionUserCommand;
import com.khodabandelu.scim.client.api.services.UserProvisionerService;
import com.khodabandelu.scim.client.api.services.UserService;
import com.khodabandelu.scim.client.domains.user.Roles;
import com.khodabandelu.scim.client.domains.user.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;

@Service
public class UserProvisionerServiceImpl implements UserProvisionerService {

    private final UserService userService;

    public UserProvisionerServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * todo
     * check user exists
     * if exists update it if externalId is equal with provisionerId
     * if exists and organizationId is changed what happen in here
     * if not exists create it
     * check provisionerId is equal with command external id
     */
    @Transactional
    @Override
    public String provisionUser(ProvisionUserCommand command, String organizationId, String provisionerId) {
        var defaultRole = new HashSet<Roles>();
        defaultRole.add(Roles.ROLE_USER);
        var user = User.builder()
                .email(command.getEmail())
                .firstname(command.getName().getFirstName())
                .lastname(command.getName().getLastName())
                .externalId(command.getId())
                .organizationId(organizationId)
                .roles(defaultRole)
                .active(false)
                .build();
        var persistedUser = this.userService.save(user);
        return persistedUser.getId();
    }


}
