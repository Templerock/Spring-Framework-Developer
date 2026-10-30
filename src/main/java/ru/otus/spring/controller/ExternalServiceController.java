package ru.otus.spring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.util.UriComponentsBuilder;
import ru.otus.spring.kafka.AuditNotificationProducer;
import ru.otus.spring.service.ExternalLibraryGateway;

import java.net.URI;
import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(path = "/api/external")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@SecurityRequirement(name = "bearerAuth")
public class ExternalServiceController {

    private final ExternalLibraryGateway gateway;
    private final AuditNotificationProducer auditNotificationProducer;

    public ExternalServiceController(ExternalLibraryGateway gateway,
                                     AuditNotificationProducer auditNotificationProducer) {
        this.gateway = gateway;
        this.auditNotificationProducer = auditNotificationProducer;
    }

    @GetMapping("/books")
    public ResponseEntity<JsonNode> getBooks(HttpServletRequest request,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return proxyNode(request, "GET", "/api/books",
                uri -> gateway.getBooks(uri, authorization(request), page, size));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<JsonNode> getBook(HttpServletRequest request, @PathVariable long id) {
        return proxyNode(request, "GET", "/api/books/" + id,
                uri -> gateway.getBook(uri, authorization(request), id));
    }

    @PostMapping("/books")
    public ResponseEntity<JsonNode> createBook(HttpServletRequest request, @RequestBody JsonNode body) {
        return proxyNode(request, "POST", "/api/books",
                uri -> gateway.createBook(uri, authorization(request), body));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(HttpServletRequest request, @PathVariable long id) {
        return proxyVoid(request, "DELETE", "/api/books/" + id,
                uri -> gateway.deleteBook(uri, authorization(request), id));
    }

    @GetMapping("/authors")
    public ResponseEntity<JsonNode> getAuthors(HttpServletRequest request) {
        return proxyNode(request, "GET", "/api/authors",
                uri -> gateway.getAuthors(uri, authorization(request)));
    }

    @GetMapping("/genres")
    public ResponseEntity<JsonNode> getGenres(HttpServletRequest request) {
        return proxyNode(request, "GET", "/api/genres",
                uri -> gateway.getGenres(uri, authorization(request)));
    }

    @GetMapping("/users")
    public ResponseEntity<JsonNode> getUsers(HttpServletRequest request) {
        return proxyNode(request, "GET", "/api/users",
                uri -> gateway.getUsers(uri, authorization(request)));
    }

    private ResponseEntity<JsonNode> proxyNode(HttpServletRequest request, String method, String relativeUri,
                                              Function<URI, ResponseEntity<JsonNode>> call) {
        String correlationId = UUID.randomUUID().toString();
        String uri = toSelfUri(request);
        auditNotificationProducer.sendIncomingCall(correlationId, method, relativeUri);
        try {
            ResponseEntity<JsonNode> response = call.apply(URI.create(uri));
            auditNotificationProducer.sendCallResult(correlationId, method, relativeUri,
                    response.getStatusCode().value(), result(response.getStatusCode().value()));
            return response;
        } catch (RuntimeException e) {
            auditNotificationProducer.sendCallResult(correlationId, method, relativeUri, 500, "ERROR");
            throw e;
        }
    }

    private ResponseEntity<Void> proxyVoid(HttpServletRequest request, String method, String relativeUri,
                                          Function<URI, ResponseEntity<Void>> call) {
        String correlationId = UUID.randomUUID().toString();
        String uri = toSelfUri(request);
        auditNotificationProducer.sendIncomingCall(correlationId, method, relativeUri);
        try {
            ResponseEntity<Void> response = call.apply(URI.create(uri));
            auditNotificationProducer.sendCallResult(correlationId, method, relativeUri,
                    response.getStatusCode().value(), result(response.getStatusCode().value()));
            return response;
        } catch (RuntimeException e) {
            auditNotificationProducer.sendCallResult(correlationId, method, relativeUri, 500, "ERROR");
            throw e;
        }
    }

    private String result(int statusCode) {
        return statusCode < 400 ? "SUCCESS" : "ERROR";
    }

    private String toSelfUri(HttpServletRequest request) {
        return UriComponentsBuilder.fromPath("")
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
