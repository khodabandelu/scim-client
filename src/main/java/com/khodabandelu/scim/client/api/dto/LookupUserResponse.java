package com.khodabandelu.scim.client.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LookupUserResponse extends BaseResponse {
    private List<UserDto> users;

    public LookupUserResponse(String message, List<UserDto> users) {
        super(message);
        this.users = users;
    }
}
