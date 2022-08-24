package com.khodabandelu.scim.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.khodabandelu.scim.client.api.commands.AuthenticateUserCommand;
import com.khodabandelu.scim.client.api.commands.CreateProvisionerCommand;
import com.khodabandelu.scim.client.api.commands.ProvisionUserCommand;
import com.khodabandelu.scim.client.api.commands.SyncProvisioningCommand;
import com.khodabandelu.scim.client.api.dto.AuthenticateUserResponse;
import com.khodabandelu.scim.client.api.dto.CreateProvisionerResponse;
import com.khodabandelu.scim.client.api.dto.ProvisionUserResponse;
import com.khodabandelu.scim.client.api.dto.SyncProvisioningResponse;
import com.khodabandelu.scim.client.api.services.UserService;
import com.khodabandelu.scim.client.domains.provisioner.ProvisionerType;
import com.khodabandelu.scim.client.domains.user.Name;
import com.khodabandelu.scim.client.domains.user.Roles;
import com.khodabandelu.scim.client.domains.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.security.Key;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Integration test for {@link com.khodabandelu.scim.client.api.controllers.UserProvisionerController}
 *
 * @author Mahdi Khodabandelu
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserProvisionerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private String secretServer;

    private String provisionerId;

    @BeforeEach
    public void setup() throws Exception {
        var roles = new HashSet<Roles>();
        roles.add(Roles.ROLE_ADMIN);
        var user = User.builder()
                .email("test@gmail.com")
                .firstname("mahdiTest")
                .lastname("KhodabandeluTest")
                .organizationId("1")
                .password(passwordEncoder.encode("test123"))
                .roles(roles)
                .active(true)
                .build();
        this.userService.save(user);

        var command = new AuthenticateUserCommand();
        command.setUsername("test@gmail.com");
        command.setPassword("test123");

        var objectMapper = new ObjectMapper();
        var jsonContent = objectMapper.writeValueAsString(command);
        var mvcResult = this.mockMvc.perform(post("/api/account/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();

        var authenticateUserResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AuthenticateUserResponse.class);

        var createProvisionerCommand = new CreateProvisionerCommand("okta", ProvisionerType.Okta);

        var createProvisionerCommandJson = objectMapper.writeValueAsString(createProvisionerCommand);
        var createProvisionerResult = this.mockMvc.perform(post("/api/provisioner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authenticateUserResponse.getToken())
                        .content(createProvisionerCommandJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();

        var createProvisionerResponse = objectMapper.readValue(createProvisionerResult.getResponse().getContentAsString(), CreateProvisionerResponse.class);

        this.provisionerId = createProvisionerResponse.getId();

        var syncProvisioningCommand = new SyncProvisioningCommand(createProvisionerResponse.getId());
        var syncProvisioningCommandJson = objectMapper.writeValueAsString(syncProvisioningCommand);
        var syncProvisioningResult = this.mockMvc.perform(post("/api/provisioner/syncProvisioning")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authenticateUserResponse.getToken())
                        .content(syncProvisioningCommandJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secret").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
        var syncProvisioningResponse = objectMapper.readValue(syncProvisioningResult.getResponse().getContentAsString(), SyncProvisioningResponse.class);
        this.secretServer = syncProvisioningResponse.getSecret();
    }

    @Test
    public void testProvisionUserEndpoint() throws Exception {

        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secretServer));
        var token = Jwts.builder()
                .setSubject("provisioner")
                .signWith(key)
                .compact();

        var provisionUserCommand = ProvisionUserCommand.builder()
                .id("1001")
                .email("userTest@external.com")
                .name(Name.builder().firstName("User").lastName("Test").build())
                .build();

        var objectMapper = new ObjectMapper();
        var provisionUserCommandJson = objectMapper.writeValueAsString(provisionUserCommand);

        var provisionUserResult = this.mockMvc.perform(post("/api/organizations/1/provisioner/" + this.provisionerId + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(provisionUserCommandJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();

        var provisionUserResponse = objectMapper.readValue(provisionUserResult.getResponse().getContentAsString(), ProvisionUserResponse.class);

        var userProvisioned = this.userService.findById(provisionUserResponse.getApplicationId());

        assertThat(userProvisioned).isNotEmpty();
        assertThat(userProvisioned.get()).isNotNull();
        assertThat(provisionUserCommand.getName().getFirstName()).isEqualTo(userProvisioned.get().getFirstname());
        assertThat(provisionUserCommand.getName().getLastName()).isEqualTo(userProvisioned.get().getLastname());
        assertThat(provisionUserCommand.getId()).isEqualTo(userProvisioned.get().getExternalId());
        assertThat(provisionUserCommand.getEmail()).isEqualTo(userProvisioned.get().getEmail());
        assertFalse(userProvisioned.get().getActive());

    }


}
