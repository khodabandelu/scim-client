package com.khodabandelu.scim.client.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SyncProvisioningResponse extends BaseResponse {
    private String secret;

    public SyncProvisioningResponse(String message, String secret) {
        super(message);
        this.secret = secret;
    }

}