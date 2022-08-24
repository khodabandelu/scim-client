package com.khodabandelu.scim.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.khodabandelu.scim.client.api.commands.AuthenticateUserCommand;
import com.khodabandelu.scim.client.api.dto.AuthenticateUserResponse;
import com.khodabandelu.scim.client.dao.UserRepository;
import com.khodabandelu.scim.client.domains.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Integration test for {@link com.khodabandelu.scim.client.api.controllers.AccountController}
 *
 * @author Mahdi Khodabandelu
 */

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class AccountControllerTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAuthenticateEndpoint() throws Exception {
        var user = User.builder()
                .email("test@gmail.com")
                .firstname("mahdiTest")
                .lastname("KhodabandeluTest")
                .password(passwordEncoder.encode("test123"))
                .active(true)
                .build();
        this.userRepository.save(user);

        var command = new AuthenticateUserCommand();
        command.setUsername("test@gmail.com");
        command.setPassword("test123");

        var jsonContent = new ObjectMapper().writeValueAsString(command);
        this.mockMvc.perform(post("/api/account/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void testAuthenticateEndpointFails() throws Exception {
        var user = User.builder()
                .email("test@gmail.com")
                .firstname("mahdiTest")
                .lastname("KhodabandeluTest")
                .password(passwordEncoder.encode("test123"))
                .active(true)
                .build();
        this.userRepository.save(user);

        var command = new AuthenticateUserCommand();
        command.setUsername("test@gmail.com");
        command.setPassword("wrong password");

        var jsonContent = new ObjectMapper().writeValueAsString(command);
        this.mockMvc.perform(post("/api/account/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void testIsAuthenticatedEndpoint() throws Exception {
        var user = User.builder()
                .email("test@gmail.com")
                .firstname("mahdiTest")
                .lastname("KhodabandeluTest")
                .password(passwordEncoder.encode("test123"))
                .active(true)
                .build();
        this.userRepository.save(user);

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

        var response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AuthenticateUserResponse.class);

        mockMvc.perform(get("/api/account/isAuthenticated")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + response.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("test@gmail.com"));
    }

    @Test
    public void testIsAuthenticatedEndpointFails() throws Exception {
        mockMvc.perform(get("/api/account/isAuthenticated")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "testuser")
    @Test
    public void testIsAuthenticatedEndpoint2() throws Exception {
        mockMvc.perform(get("/api/account/isAuthenticated")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
