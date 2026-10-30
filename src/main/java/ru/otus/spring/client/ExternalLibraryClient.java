package ru.otus.spring.client;

import com.fasterxml.jackson.databind.JsonNode;
import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@FeignClient(name = "library-internal")
@Headers("Accept: application/json")
public interface ExternalLibraryClient {

    @GetMapping("/api/books")
    Response getBooks(URI uri,
                      @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                      @RequestParam int page,
                      @RequestParam int size);

    @GetMapping("/api/books/{id}")
    Response getBook(URI uri,
                     @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                     @PathVariable long id);

    @PostMapping("/api/books")
    Response createBook(URI uri,
                        @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                        @RequestBody JsonNode body);

    @DeleteMapping("/api/books/{id}")
    Response deleteBook(URI uri,
                        @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                        @PathVariable long id);

    @GetMapping("/api/authors")
    Response getAuthors(URI uri,
                        @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization);

    @GetMapping("/api/genres")
    Response getGenres(URI uri,
                       @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization);

    @GetMapping("/api/users")
    Response getUsers(URI uri,
                      @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization);
}
