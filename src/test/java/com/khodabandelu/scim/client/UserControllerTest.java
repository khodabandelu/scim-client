package com.khodabandelu.scim.client;


import com.khodabandelu.scim.client.api.services.UserService;
import com.khodabandelu.scim.client.domains.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Integration test for {@link com.khodabandelu.scim.client.api.controllers.UserProvisionerController}
 *
 * @author Mahdi Khodabandelu
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @WithMockCustomUser(username = "admin", roles = "ROLE_ADMIN", organizationId = "1")
    @Test
    public void testActivateUserEndpoint() throws Exception {
        var user = User.builder()
                .email("test@gmail.com")
                .firstname("test")
                .lastname("user")
                .organizationId("1")
                .active(false)
                .build();
        var persistedUser = this.userService.save(user);


        var provisionUserResult = this.mockMvc.perform(post("/api/users/activate/" + persistedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();

        var userActivated = this.userService.findById(persistedUser.getId());

        assertThat(userActivated).isNotEmpty();
        assertThat(userActivated.get()).isNotNull();
        assertTrue(userActivated.get().getActive());
    }

    @WithMockCustomUser(username = "admin", roles = "ROLE_ROOT", organizationId = "1")
    @Test
    public void getAllUsers() throws Exception {
        var user = User.builder()
                .email("test@gmail.com")
                .firstname("test")
                .lastname("user")
                .organizationId("1")
                .active(false)
                .build();
        var persistedUser = this.userService.save(user);
        // Get all the users
        this.mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.users.[*].email").value(hasItem("test@gmail.com")))
                .andExpect(jsonPath("$.users.[*].firstname").value(hasItem("test")))
                .andExpect(jsonPath("$.users.[*].lastname").value(hasItem("user")))
                .andExpect(jsonPath("$.users.[*].organizationId").value(hasItem("1")))
                .andExpect(jsonPath("$.users.[*].active").value(hasItem(false)));
    }

    @WithMockCustomUser(username = "admin", roles = "ROLE_ADMIN", organizationId = "1")
    @Test
    public void getAllUsersByOrganization() throws Exception {
        var user = User.builder()
                .email("test@gmail.com")
                .firstname("test")
                .lastname("user")
                .organizationId("1")
                .active(false)
                .build();

        var user2 = User.builder()
                .email("test2@gmail.com")
                .firstname("test2")
                .lastname("user2")
                .organizationId("2")
                .active(false)
                .build();
        this.userService.save(user);
        this.userService.save(user2);
        // Get all the users
        this.mockMvc.perform(get("/api/users/byOrganization")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.users.[*].email").value(hasItem("test@gmail.com")))
                .andExpect(jsonPath("$.users.[*].firstname").value(hasItem("test")))
                .andExpect(jsonPath("$.users.[*].lastname").value(hasItem("user")))
                .andExpect(jsonPath("$.users.[*].organizationId").value(hasItem("1")))
                .andExpect(jsonPath("$.users.[*].active").value(hasItem(false)));
    }


}
