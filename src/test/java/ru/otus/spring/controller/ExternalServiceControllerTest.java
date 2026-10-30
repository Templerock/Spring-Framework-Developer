package ru.otus.spring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1, topics = "notification-command")
class ExternalServiceControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void anonymousRequestIsRejected() {
        ResponseEntity<String> response = rest.getForEntity("/api/external/books", String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void booksListIsForwarded() throws Exception {
        String resp = exchange("/api/external/books?page=0&size=20", HttpMethod.GET, null, token("admin", "admin"));

        JsonNode root = objectMapper.readTree(resp);
        assertThat(root.path("_embedded").path("books").isArray()).isTrue();
        assertThat(root.path("_links").path("self").path("href").asText())
                .contains("/api/books");
    }

    @Test
    void singleBookIsForwardedWithLinks() throws Exception {
        long id = createBookDirect(token("admin", "admin"));

        ResponseEntity<String> response = rest.exchange("/api/external/books/" + id, HttpMethod.GET,
                new HttpEntity<>(headers(token("user", "user"))), String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.path("title").asText()).isNotBlank();
        assertThat(root.path("_links").path("author").path("href").asText())
                .isEqualTo("http://localhost:" + port + "/api/books/" + id + "/author");
    }

    @Test
    void createBookIsForwardedAndReturnsCreated() throws Exception {
        String body = "{\"title\":\"Proxy Book\",\"author\":\"/api/authors/1\",\"genre\":\"/api/genres/1\"}";
        ResponseEntity<String> response = exchangeEntity("/api/external/books", HttpMethod.POST, body, token("admin", "admin"));

        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    void deleteBookRepeatsDirectBehaviorThroughProxy() throws Exception {
        ResponseEntity<String> proxied = rest.exchange("/api/external/books/" + createBookDirect(token("admin", "admin")),
                HttpMethod.DELETE, new HttpEntity<>(headers(token("admin", "admin"))), String.class);
        ResponseEntity<String> direct = rest.exchange("/api/books/" + createBookDirect(token("admin", "admin")),
                HttpMethod.DELETE, new HttpEntity<>(headers(token("admin", "admin"))), String.class);

        assertThat(proxied.getStatusCode()).isEqualTo(direct.getStatusCode());
        assertThat(proxied.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void missingResourceStatusIsForwardedBackThroughProxy() throws Exception {
        ResponseEntity<String> response = rest.exchange("/api/external/books/999999", HttpMethod.GET,
                new HttpEntity<>(headers(token("admin", "admin"))), String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void adminOnlyResourceIsForbiddenForRegularUserThroughProxy() throws Exception {
        ResponseEntity<String> response = rest.exchange("/api/external/users", HttpMethod.GET,
                new HttpEntity<>(headers(token("user", "user"))), String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }

    private long createBookDirect(String token) throws Exception {
        String body = "{\"title\":\"Proxy Book\",\"author\":\"/api/authors/1\",\"genre\":\"/api/genres/1\"}";
        HttpHeaders headers = headers(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = rest.exchange("/api/books", HttpMethod.POST,
                new HttpEntity<>(body, headers), String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
        String id = location.substring(location.lastIndexOf('/') + 1);
        return Long.parseLong(id);
    }

    private String token(String username, String password) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("username", username, "password", password));
        String login = exchange("/api/auth/login", HttpMethod.POST, body, null);
        return objectMapper.readTree(login).path("token").asText();
    }

    private HttpHeaders headers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private String exchange(String path, HttpMethod method, String body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return rest.exchange(path, method, new HttpEntity<>(body, headers), String.class).getBody();
    }

    private ResponseEntity<String> exchangeEntity(String path, HttpMethod method, String body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return rest.exchange(path, method, new HttpEntity<>(body, headers), String.class);
    }
}
