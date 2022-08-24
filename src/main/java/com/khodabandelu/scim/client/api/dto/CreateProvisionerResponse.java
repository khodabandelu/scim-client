package com.khodabandelu.scim.client.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreateProvisionerResponse extends BaseResponse {
    private String id;

    public CreateProvisionerResponse(String message, String id) {
        super(message);
        this.id = id;
    }
}