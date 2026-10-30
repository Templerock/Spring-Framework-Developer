package ru.otus.spring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.otus.spring.kafka.AuditNotificationProducer;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/external")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@SecurityRequirement(name = "bearerAuth")
public class ExternalServiceController {

    private final RestClient restClient;
    private final AuditNotificationProducer auditNotificationProducer;

    public ExternalServiceController(RestClient.Builder builder,
                                    AuditNotificationProducer auditNotificationProducer) {
        this.restClient = builder
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.auditNotificationProducer = auditNotificationProducer;
    }

    @GetMapping("/books")
    public ResponseEntity<JsonNode> getBooks(HttpServletRequest request,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return proxy(request, HttpMethod.GET, "/api/books?page=" + page + "&size=" + size, null);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<JsonNode> getBook(HttpServletRequest request, @PathVariable long id) {
        return proxy(request, HttpMethod.GET, "/api/books/" + id, null);
    }

    @PostMapping("/books")
    public ResponseEntity<JsonNode> createBook(HttpServletRequest request, @RequestBody JsonNode body) {
        return proxy(request, HttpMethod.POST, "/api/books", body);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(HttpServletRequest request, @PathVariable long id) {
        return proxyVoid(request, HttpMethod.DELETE, "/api/books/" + id);
    }

    @GetMapping("/authors")
    public ResponseEntity<JsonNode> getAuthors(HttpServletRequest request) {
        return proxy(request, HttpMethod.GET, "/api/authors", null);
    }

    @GetMapping("/genres")
    public ResponseEntity<JsonNode> getGenres(HttpServletRequest request) {
        return proxy(request, HttpMethod.GET, "/api/genres", null);
    }

    @GetMapping("/users")
    public ResponseEntity<JsonNode> getUsers(HttpServletRequest request) {
        return proxy(request, HttpMethod.GET, "/api/users", null);
    }

    private ResponseEntity<JsonNode> proxy(HttpServletRequest request, HttpMethod method, String relativeUri, JsonNode body) {
        String correlationId = UUID.randomUUID().toString();
        String uri = toSelfUri(request, relativeUri);
        auditNotificationProducer.sendIncomingCall(correlationId, method.name(), relativeUri);
        try {
            var call = restClient.method(method)
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, authorization(request));
            if (body != null) {
                call.contentType(MediaType.APPLICATION_JSON).body(body);
            }
            ResponseEntity<JsonNode> response = call.retrieve()
                    .onStatus(status -> true, (req, res) -> { })
                    .toEntity(JsonNode.class);
            auditNotificationProducer.sendCallResult(correlationId, method.name(), relativeUri,
                    response.getStatusCode().value(), result(response.getStatusCode().value()));
            return response;
        } catch (RuntimeException e) {
            auditNotificationProducer.sendCallResult(correlationId, method.name(), relativeUri, 500, "ERROR");
            throw e;
        }
    }

    private ResponseEntity<Void> proxyVoid(HttpServletRequest request, HttpMethod method, String relativeUri) {
        String correlationId = UUID.randomUUID().toString();
        String uri = toSelfUri(request, relativeUri);
        auditNotificationProducer.sendIncomingCall(correlationId, method.name(), relativeUri);
        try {
            ResponseEntity<Void> response = restClient.method(method)
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, authorization(request))
                    .retrieve()
                    .onStatus(status -> true, (req, res) -> { })
                    .toBodilessEntity();
            auditNotificationProducer.sendCallResult(correlationId, method.name(), relativeUri,
                    response.getStatusCode().value(), result(response.getStatusCode().value()));
            return response;
        } catch (RuntimeException e) {
            auditNotificationProducer.sendCallResult(correlationId, method.name(), relativeUri, 500, "ERROR");
            throw e;
        }
    }

    private String result(int statusCode) {
        return statusCode < 400 ? "SUCCESS" : "ERROR";
    }

    private String toSelfUri(HttpServletRequest request, String relativeUri) {
        return UriComponentsBuilder.fromUriString(relativeUri)
                .scheme(request.getScheme())
                .host(request.getServerName())
                .port(request.getServerPort())
                .build()
                .toUriString();
    }

    private String authorization(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return header == null ? "" : header;
    }
}
