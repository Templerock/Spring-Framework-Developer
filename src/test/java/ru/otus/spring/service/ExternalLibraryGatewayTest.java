package ru.otus.spring.service;

import com.fasterxml.jackson.databind.JsonNode;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.otus.spring.client.ExternalLibraryClient;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalLibraryGatewayTest {

    private static final URI SELF = URI.create("http://localhost:8080");

    @Mock
    private ExternalLibraryClient client;

    @InjectMocks
    private ExternalLibraryGateway gateway;

    @Test
    void getBooksForwardsSuccessfulResponse() {
        try (Response response = Response.builder()
                .status(200)
                .reason("OK")
                .request(feign.Request.create(feign.Request.HttpMethod.GET, "/api/books", java.util.Map.of(), new byte[0], null))
                .body("{\"value\":1}", java.nio.charset.StandardCharsets.UTF_8)
                .build()) {
            org.mockito.Mockito.when(client.getBooks(any(), any(), anyInt(), anyInt())).thenReturn(response);

            ResponseEntity<JsonNode> result = gateway.getBooks(SELF, "Bearer t", 0, 20);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().path("value").asInt()).isEqualTo(1);
        }
    }

    @Test
    void fallbackReturnsServiceUnavailableWhenClientReturnsNull() {
        org.mockito.Mockito.when(client.getBook(any(), any(), eq(42L))).thenReturn(null);

        ResponseEntity<JsonNode> result = gateway.getBook(SELF, "Bearer t", 42);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
