package com.khodabandelu.scim.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khodabandelu.scim.client.api.commands.CreateProvisionerCommand;
import com.khodabandelu.scim.client.api.commands.SyncProvisioningCommand;
import com.khodabandelu.scim.client.api.dto.CreateProvisionerResponse;
import com.khodabandelu.scim.client.domains.provisioner.ProvisionerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Integration test for {@link com.khodabandelu.scim.client.api.controllers.ProvisionerController}
 *
 * @author Mahdi Khodabandelu
 */

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class ProvisionerControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @WithMockCustomUser(username = "admin", roles = "ROLE_ADMIN")
    @Test
    public void testCreateProvisionerEndpoint() throws Exception {
        var command = new CreateProvisionerCommand("okta", ProvisionerType.Okta);

        var objectMapper = new ObjectMapper();
        var jsonContent = objectMapper.writeValueAsString(command);
        this.mockMvc.perform(post("/api/provisioner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @WithMockUser(username = "user")
    @Test
    public void testSyncProvisioningEndpointNormalUser() throws Exception {
        var command = new SyncProvisioningCommand("1");

        var objectMapper = new ObjectMapper();
        var jsonContent = objectMapper.writeValueAsString(command);
        this.mockMvc.perform(post("/api/provisioner/syncProvisioning")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isForbidden());
    }

    @WithMockCustomUser(username = "admin", roles = "ROLE_ADMIN", organizationId = "1")
    @Test
    public void testSyncProvisioningEndpoint() throws Exception {
        var createProvisionerCommand = new CreateProvisionerCommand("okta", ProvisionerType.Okta);

        var objectMapper = new ObjectMapper();
        var createProvisionerCommandJson = objectMapper.writeValueAsString(createProvisionerCommand);
        var createProvisionerResult = this.mockMvc.perform(post("/api/provisioner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProvisionerCommandJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();

        var createProvisionerResponse = objectMapper.readValue(createProvisionerResult.getResponse().getContentAsString(), CreateProvisionerResponse.class);

        var command = new SyncProvisioningCommand(createProvisionerResponse.getId());

        var jsonContent = objectMapper.writeValueAsString(command);
        this.mockMvc.perform(post("/api/provisioner/syncProvisioning")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secret").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @WithMockCustomUser(username = "admin", roles = "ROLE_ADMIN", organizationId = "")
    @Test
    public void testSyncProvisioningEndpointFailsDueToEmptyOrganizationId() throws Exception {
        var command = new SyncProvisioningCommand("1");

        var objectMapper = new ObjectMapper();
        var jsonContent = objectMapper.writeValueAsString(command);
        this.mockMvc.perform(post("/api/provisioner/syncProvisioning")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.secret").doesNotExist())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

}

