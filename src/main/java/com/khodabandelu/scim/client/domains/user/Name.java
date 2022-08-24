package com.khodabandelu.scim.client.domains.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Name {
    private String firstName;
    private String lastName;
}
