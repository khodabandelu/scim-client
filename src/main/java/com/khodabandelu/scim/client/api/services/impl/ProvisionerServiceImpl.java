package com.khodabandelu.scim.client.api.services.impl;

import com.khodabandelu.scim.client.api.commands.CreateProvisionerCommand;
import com.khodabandelu.scim.client.api.commands.SyncProvisioningCommand;
import com.khodabandelu.scim.client.api.services.ProvisionerService;
import com.khodabandelu.scim.client.dao.ProvisionerRepository;
import com.khodabandelu.scim.client.dao.ProvisionerSecretRepository;
import com.khodabandelu.scim.client.domains.provisioner.Provisioner;
import com.khodabandelu.scim.client.domains.provisioner.ProvisionerSecret;
import com.khodabandelu.scim.client.domains.user.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

/**
 * Service class for managing users.
 *
 * @author Mahdi Khodabandelu
 */
@Service
public class ProvisionerServiceImpl implements ProvisionerService {

    private final ProvisionerRepository provisionerRepository;
    private final ProvisionerSecretRepository provisionerSecretRepository;

    public ProvisionerServiceImpl(ProvisionerRepository provisionerRepository, ProvisionerSecretRepository provisionerSecretRepository) {
        this.provisionerRepository = provisionerRepository;
        this.provisionerSecretRepository = provisionerSecretRepository;
    }

    @Transactional
    @Override
    public Provisioner save(CreateProvisionerCommand command) {
        var provisioner = Provisioner.builder()
                .provisionerType(command.getProvisionerType())
                .name(command.getName())
                .build();
        return this.provisionerRepository.save(provisioner);
    }

    @Transactional
    @Override
    public String syncProvisioning(SyncProvisioningCommand command) {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = (User) securityContext.getAuthentication().getPrincipal();

        if (!StringUtils.hasText(command.getProvisionerId())) {
            throw new IllegalStateException("Provisioner Id should be filled");
        }

        this.provisionerRepository.findById(command.getProvisionerId()).orElseThrow(() -> {
            throw new IllegalStateException("This provisioner is not found!");
        });

        if (!StringUtils.hasText(user.getOrganizationId())) {
            throw new IllegalStateException("Organization Id should not be empty");
        }

        var randomString = generateRandomAlphaNumeric();
        var secret = Base64.getEncoder().encodeToString(randomString.getBytes());
        var provisionerSecret = ProvisionerSecret.builder()
                .secret(secret)
                .provisionerId(command.getProvisionerId())
                .organizationId(user.getOrganizationId())
                .build();
        provisionerSecretRepository.save(provisionerSecret);
        return secret;
    }

    @Override
    public Optional<ProvisionerSecret> findByOrganizationIdAndProvisionerId(String organizationID, String provisionerId) {
        return this.provisionerSecretRepository.findByOrganizationIdAndProvisionerId(organizationID, provisionerId);
    }

    /**
     * todo add alhpa numeric utils
     */
    private String generateRandomAlphaNumeric() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 256;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
