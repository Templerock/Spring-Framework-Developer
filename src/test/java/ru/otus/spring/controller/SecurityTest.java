package ru.otus.spring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void resourcesAreProtectedForAnonymous() throws Exception {
        mockMvc.perform(get("/api/authors")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/genres")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/books")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/comments")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginReturnsJwtForValidCredentials() throws Exception {
        String token = login("admin", "admin");

        mockMvc.perform(get("/api/authors").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerUiIsAccessibleForAnonymous() throws Exception {
        mockMvc.perform(get("/swagger-ui.html")).andExpect(status().isFound());
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    @Test
    void loginFailsForInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usersApiIsForbiddenForRegularUser() throws Exception {
        String token = login("user", "user");

        mockMvc.perform(get("/api/authors").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void usersApiIsAccessibleForAdmin() throws Exception {
        String token = login("admin", "admin");

        mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void authenticatedUserWithAdminRoleCanAccessLibraryResources() throws Exception {
        mockMvc.perform(get("/api/authors")).andExpect(status().isOk());
        mockMvc.perform(get("/api/books")).andExpect(status().isOk());
        mockMvc.perform(get("/api/users")).andExpect(status().isOk());
    }

    private String login(String username, String password) throws Exception {
        String content = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode node = objectMapper.readTree(content);
        return node.path("token").asText();
    }
}
