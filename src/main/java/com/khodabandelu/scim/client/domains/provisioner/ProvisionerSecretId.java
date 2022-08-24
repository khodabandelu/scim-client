package com.khodabandelu.scim.client.domains.provisioner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvisionerSecretId implements Serializable {
    private String organizationId;
    private String provisionerId;
}
