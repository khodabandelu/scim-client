package com.khodabandelu.scim.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khodabandelu.scim.client.api.commands.CreateProvisionerCommand;
import com.khodabandelu.scim.client.domains.provisioner.ProvisionerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

}

