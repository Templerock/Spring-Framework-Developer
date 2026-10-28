package ru.otus.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authorCrud() throws Exception {
        long authorId = postId(post("/api/authors")
                .contentType(APPLICATION_JSON)
                .content("{\"firstName\":\"Rest\",\"lastName\":\"Author\"}"));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/authors/{id}", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Author"));
        mockMvc.perform(put("/api/authors/{id}", authorId)
                        .contentType(APPLICATION_JSON)
                        .content("{\"firstName\":\"Rest\",\"lastName\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"));

        mockMvc.perform(delete("/api/authors/{id}", authorId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/authors/{id}", authorId))
                .andExpect(status().isNotFound());
    }

    @Test
    void authorNotFound() throws Exception {
        mockMvc.perform(get("/api/authors/{id}", 999_999_999))
                .andExpect(status().isNotFound());
    }

    @Test
    void genreCrud() throws Exception {
        long genreId = postId(post("/api/genres")
                .contentType(APPLICATION_JSON)
                .content("{\"name\":\"RestGenre\"}"));

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/genres/{id}", genreId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("RestGenre"));
        mockMvc.perform(put("/api/genres/{id}", genreId)
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"RestGenreNew\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("RestGenreNew"));

        mockMvc.perform(delete("/api/genres/{id}", genreId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/genres/{id}", genreId))
                .andExpect(status().isNotFound());
    }

    @Test
    void genreNotFound() throws Exception {
        mockMvc.perform(get("/api/genres/{id}", 999_999_999))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookCrud() throws Exception {
        long authorId = postId(post("/api/authors")
                .contentType(APPLICATION_JSON)
                .content("{\"firstName\":\"Book\",\"lastName\":\"Writer\"}"));
        long genreId = postId(post("/api/genres")
                .contentType(APPLICATION_JSON)
                .content("{\"name\":\"BookGenre\"}"));

        long bookId = postId(post("/api/books")
                .contentType(APPLICATION_JSON)
                .content("{\"title\":\"Rest Book\",\"authorId\":" + authorId + ",\"genreId\":" + genreId + "}"));

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Rest Book"))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.genreId").value(genreId));
        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\":\"Updated Book\",\"authorId\":" + authorId + ",\"genreId\":" + genreId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));

        mockMvc.perform(delete("/api/books/{id}", bookId)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/books/{id}", bookId)).andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/authors/{id}", authorId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/genres/{id}", genreId)).andExpect(status().isNoContent());
    }

    @Test
    void bookCreateWithMissingAuthorIsNotFound() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\":\"No Book\",\"authorId\":999999999,\"genreId\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void commentCrud() throws Exception {
        long authorId = postId(post("/api/authors")
                .contentType(APPLICATION_JSON)
                .content("{\"firstName\":\"Comment\",\"lastName\":\"Writer\"}"));
        long genreId = postId(post("/api/genres")
                .contentType(APPLICATION_JSON)
                .content("{\"name\":\"CommentGenre\"}"));
        long bookId = postId(post("/api/books")
                .contentType(APPLICATION_JSON)
                .content("{\"title\":\"Comment Book\",\"authorId\":" + authorId + ",\"genreId\":" + genreId + "}"));

        long commentId = postId(post("/api/comments")
                .contentType(APPLICATION_JSON)
                .content("{\"text\":\"N1\",\"bookId\":" + bookId + "}"));

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/comments/{id}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("N1"))
                .andExpect(jsonPath("$.bookId").value(bookId));
        mockMvc.perform(put("/api/comments/{id}", commentId)
                        .contentType(APPLICATION_JSON)
                        .content("{\"text\":\"N2\",\"bookId\":" + bookId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("N2"));

        mockMvc.perform(delete("/api/comments/{id}", commentId)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/comments/{id}", commentId)).andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/books/{id}", bookId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/authors/{id}", authorId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/genres/{id}", genreId)).andExpect(status().isNoContent());
    }

    private long postId(MockHttpServletRequestBuilder request) throws Exception {
        String content = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(content).path("id").asLong();
    }
}
