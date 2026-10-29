package ru.otus.spring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = "USER")
class LibraryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authorRepositoryReturnsHateoasEmbedded() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.authors").isArray())
                .andExpect(jsonPath("$._embedded.authors[0]._links.self.href").isString());
    }

    @Test
    void bookRepositoryReturnsSelfLinkOnCreate() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.books").isArray())
                .andExpect(jsonPath("$._embedded.books[0]._links.self.href").isString());
    }

    @Test
    void repositoryRootsExposedWithLinks() throws Exception {
        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links").exists());
    }
}
