package com.khodabandelu.scim.client.domains.provisioner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@IdClass(ProvisionerSecretId.class)
public class ProvisionerSecret {

    @Id
    @Column(name = "organization_id")
    private String organizationId;

    @Id
    @Column(name = "provisioner_id")
    private String provisionerId;

    @Column(name = "secret", length = 500)
    private String secret;

}
