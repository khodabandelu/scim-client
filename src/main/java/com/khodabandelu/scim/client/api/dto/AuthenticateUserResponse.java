package com.khodabandelu.scim.client.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthenticateUserResponse extends BaseResponse {
    private String token;

    public AuthenticateUserResponse(String message, String token) {
        super(message);
        this.token = token;
    }
}
