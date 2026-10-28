package ru.otus.spring.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.domain.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AuthorDao.class)
class AuthorDaoTest {

    @Autowired
    private AuthorDao authorDao;

    @Test
    void savedAuthorCanBeReadById() {
        var author = authorDao.save(new Author(0, "Lev", "Tolstoy"));

        assertThat(author.getId()).isPositive();
        assertThat(authorDao.getById(author.getId()))
                .get()
                .extracting(Author::getFirstName, Author::getLastName)
                .containsExactly("Lev", "Tolstoy");
    }

    @Test
    void getAllReturnsSeededAndNewlySavedAuthors() {
        assertThat(authorDao.getAll()).isNotEmpty();

        var author = authorDao.save(new Author(0, "Fyodor", "Dostoevsky"));

        assertThat(authorDao.getAll())
                .extracting(Author::getId)
                .contains(author.getId());
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(authorDao.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesFirstAndLastName() {
        var author = authorDao.save(new Author(0, "Old", "Name"));

        var updated = authorDao.save(new Author(author.getId(), "New", "Name"));

        assertThat(updated.getFirstName()).isEqualTo("New");
        assertThat(authorDao.getById(author.getId()))
                .get()
                .extracting(Author::getFirstName, Author::getLastName)
                .containsExactly("New", "Name");
    }

    @Test
    void deleteRemovesAuthor() {
        var author = authorDao.save(new Author(0, "To", "Delete"));

        authorDao.delete(author);

        assertThat(authorDao.getById(author.getId())).isEmpty();
    }

    @Test
    void countMatchesInsertions() {
        long before = authorDao.count();

        authorDao.save(new Author(0, "One", "Author"));
        authorDao.save(new Author(0, "Two", "Author"));

        assertThat(authorDao.count()).isEqualTo(before + 2);
    }
}
