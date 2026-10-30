package ru.otus.spring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.otus.spring.client.ExternalLibraryClient;

import java.io.IOException;
import java.net.URI;

@Service
public class ExternalLibraryGateway {

    private static final Logger log = LoggerFactory.getLogger(ExternalLibraryGateway.class);

    private final ExternalLibraryClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExternalLibraryGateway(ExternalLibraryClient client) {
        this.client = client;
    }

    @CircuitBreaker(name = "library", fallbackMethod = "getBooksFallback")
    public ResponseEntity<JsonNode> getBooks(URI uri, String authorization, int page, int size) {
        return readEntity(client.getBooks(uri, authorization, page, size));
    }

    @CircuitBreaker(name = "library", fallbackMethod = "getBookFallback")
    public ResponseEntity<JsonNode> getBook(URI uri, String authorization, long id) {
        return readEntity(client.getBook(uri, authorization, id));
    }

    @CircuitBreaker(name = "library", fallbackMethod = "createBookFallback")
    public ResponseEntity<JsonNode> createBook(URI uri, String authorization, JsonNode body) {
        return readEntity(client.createBook(uri, authorization, body));
    }

    @CircuitBreaker(name = "library", fallbackMethod = "deleteBookFallback")
    public ResponseEntity<Void> deleteBook(URI uri, String authorization, long id) {
        return toEmpty(client.deleteBook(uri, authorization, id));
    }

    @CircuitBreaker(name = "library", fallbackMethod = "getAuthorsFallback")
    public ResponseEntity<JsonNode> getAuthors(URI uri, String authorization) {
        return readEntity(client.getAuthors(uri, authorization));
    }

    @CircuitBreaker(name = "library", fallbackMethod = "getGenresFallback")
    public ResponseEntity<JsonNode> getGenres(URI uri, String authorization) {
        return readEntity(client.getGenres(uri, authorization));
    }

    @CircuitBreaker(name = "library", fallbackMethod = "getUsersFallback")
    public ResponseEntity<JsonNode> getUsers(URI uri, String authorization) {
        return readEntity(client.getUsers(uri, authorization));
    }

    private ResponseEntity<JsonNode> readEntity(Response response) {
        if (response == null) {
            return unavailable();
        }
        try (Response ignored = response) {
            JsonNode body = response.body() == null ? null : objectMapper.readTree(response.body().asInputStream());
            return ResponseEntity.status(response.status()).body(body);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read response body", e);
        }
    }

    private ResponseEntity<Void> toEmpty(Response response) {
        if (response == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        try (Response ignored = response) {
            return ResponseEntity.status(response.status()).build();
        }
    }

    private ResponseEntity<JsonNode> unavailable() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(objectMapper.nullNode());
    }

    private ResponseEntity<JsonNode> getBooksFallback(URI uri, String authorization, int page, int size, Throwable t) {
        return fallback(t, "GET /api/books");
    }

    private ResponseEntity<JsonNode> getBookFallback(URI uri, String authorization, long id, Throwable t) {
        return fallback(t, "GET /api/books/" + id);
    }

    private ResponseEntity<JsonNode> createBookFallback(URI uri, String authorization, JsonNode body, Throwable t) {
        return fallback(t, "POST /api/books");
    }

    private ResponseEntity<Void> deleteBookFallback(URI uri, String authorization, long id, Throwable t) {
        log.warn("Circuit 'library' fallback for DELETE /api/books/{}", id, t);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private ResponseEntity<JsonNode> getAuthorsFallback(URI uri, String authorization, Throwable t) {
        return fallback(t, "GET /api/authors");
    }

    private ResponseEntity<JsonNode> getGenresFallback(URI uri, String authorization, Throwable t) {
        return fallback(t, "GET /api/genres");
    }

    private ResponseEntity<JsonNode> getUsersFallback(URI uri, String authorization, Throwable t) {
        return fallback(t, "GET /api/users");
    }

    private ResponseEntity<JsonNode> fallback(Throwable t, String operation) {
        log.warn("Circuit 'library' fallback for {}: {}", operation, t == null ? "circuit open" : t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(objectMapper.createObjectNode().put("error", "fallback: " + operation));
    }
}
