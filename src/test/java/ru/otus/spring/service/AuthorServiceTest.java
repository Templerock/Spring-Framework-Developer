package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.domain.Author;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AuthorServiceTest {

    @Autowired
    private AuthorService authorService;

    @Test
    void addReturnsCreatedAuthorWithId() {
        var author = authorService.add("Lev", "Tolstoy");

        assertThat(author.getId()).isPositive();
        assertThat(authorService.getById(author.getId()))
                .get()
                .extracting(Author::getFirstName, Author::getLastName)
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

        var updated = authorService.update(author.getId(), "New", "Name");

        assertThat(updated)
                .get()
                .extracting(Author::getFirstName, Author::getLastName)
                .containsExactly("New", "Name");
    }

    @Test
    void deleteByIdRemovesAuthor() {
        var author = authorService.add("To", "Delete");

        authorService.deleteById(author.getId());

        assertThat(authorService.getById(author.getId())).isEmpty();
    }
}
