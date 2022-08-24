package com.khodabandelu.scim.client.api.dto;

import com.khodabandelu.scim.client.domains.user.Roles;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDto {
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String organizationId;
    private String externalId;
    private Boolean active;
    private Set<Roles> roles;
}
