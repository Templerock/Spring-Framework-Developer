package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.dao.AuthorDao;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({AuthorService.class, AuthorDao.class})
class AuthorServiceTest {

    @Autowired
    private AuthorService authorService;

    @Test
    void addReturnsCreatedAuthorWithId() {
        var author = authorService.add("Lev", "Tolstoy");

        assertThat(author.getId()).isPositive();
        assertThat(authorService.getById(author.getId()))
                .get()
                .extracting(a -> a.getFirstName(), a -> a.getLastName())
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
                .extracting(a -> a.getFirstName(), a -> a.getLastName())
                .containsExactly("New", "Name");
    }

    @Test
    void deleteByIdRemovesAuthor() {
        var author = authorService.add("To", "Delete");

        authorService.deleteById(author.getId());

        assertThat(authorService.getById(author.getId())).isEmpty();
    }
}
