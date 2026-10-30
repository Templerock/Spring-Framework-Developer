package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dto.AuthorDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
@Transactional
class AuthorServiceTest {

    @Autowired
    private AuthorService authorService;

    @Test
    void addReturnsCreatedAuthorWithId() {
        var author = authorService.add("Lev", "Tolstoy");

        assertThat(author.id()).isPositive();
        assertThat(authorService.getById(author.id()))
                .get()
                .extracting(AuthorDto::firstName, AuthorDto::lastName)
                .containsExactly("Lev", "Tolstoy");
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(authorService.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateReturnsEmptyForUnknownId() {
        assertThat(authorService.update(Long.MAX_VALUE, "New", "Name")).isEmpty();
    }

    @Test
    void updateChangesExistingAuthor() {
        var author = authorService.add("Old", "Name");

        var updated = authorService.update(author.id(), "New", "Name");

        assertThat(updated)
                .get()
                .extracting(AuthorDto::firstName, AuthorDto::lastName)
                .containsExactly("New", "Name");
    }

    @Test
    void deleteByIdRemovesAuthor() {
        var author = authorService.add("To", "Delete");

        authorService.deleteById(author.id());

        assertThat(authorService.getById(author.id())).isEmpty();
    }
}
