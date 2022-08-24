package com.khodabandelu.scim.client.api.dto;

import com.khodabandelu.scim.client.domains.user.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProvisionUserResponse extends BaseResponse {
    private String id;
    private String email;
    private Name name;
    private String applicationId;

    public ProvisionUserResponse(String message, String id) {
        super(message);
        this.id = id;
    }

}