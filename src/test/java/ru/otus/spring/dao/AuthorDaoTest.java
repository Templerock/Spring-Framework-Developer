package ru.otus.spring.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.otus.spring.domain.Author;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import(AuthorDao.class)
class AuthorDaoTest {

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Test
    void insertedAuthorCanBeReadById() {
        var author = new Author(0, "Lev", "Tolstoy");

        long id = authorDao.insert(author);

        assertThat(id).isPositive();
        assertThat(authorDao.getById(id))
                .get()
                .extracting(Author::getFirstName, Author::getLastName)
                .containsExactly("Lev", "Tolstoy");
    }

    @Test
    void getAllReturnsSeededAndNewlyInsertedAuthors() {
        assertThat(authorDao.getAll()).isNotEmpty();

        long id = authorDao.insert(new Author(0, "Fyodor", "Dostoevsky"));

        assertThat(authorDao.getAll())
                .extracting(Author::getId)
                .contains(id);
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(authorDao.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesFirstAndLastName() {
        long id = authorDao.insert(new Author(0, "Old", "Name"));

        authorDao.update(new Author(id, "New", "Name"));

        assertThat(authorDao.getById(id))
                .get()
                .extracting(Author::getFirstName, Author::getLastName)
                .containsExactly("New", "Name");
    }

    @Test
    void deleteByIdRemovesAuthor() {
        long id = authorDao.insert(new Author(0, "To", "Delete"));

        authorDao.deleteById(id);

        assertThat(authorDao.getById(id)).isEmpty();
    }

    @Test
    void deleteAuthorReferencedByBookThrows() {
        long authorId = insertAuthor("Referenced", "Author");
        insertBook("Referenced Book", authorId);
        assertThatThrownBy(() -> authorDao.deleteById(authorId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deletingUnknownAuthorIsNoOp() {
        assertThatCode(() -> authorDao.deleteById(Long.MAX_VALUE)).doesNotThrowAnyException();
    }

    @Test
    void countMatchesInsertions() {
        long before = authorDao.count();

        authorDao.insert(new Author(0, "One", "Author"));
        authorDao.insert(new Author(0, "Two", "Author"));

        assertThat(authorDao.count()).isEqualTo(before + 2);
    }

    private long insertAuthor(String firstName, String lastName) {
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update("""
                        insert into authors (first_name, last_name)
                        values (:first_name, :last_name)
                        """,
                new MapSqlParameterSource()
                        .addValue("first_name", firstName)
                        .addValue("last_name", lastName),
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    private void insertBook(String title, long authorId) {
        long genreId = insertGenre("Legend");        jdbc.update("""
                        insert into books (title, author_id, genre_id)
                        values (:title, :author_id, :genre_id)
                        """,
                new MapSqlParameterSource()
                        .addValue("title", title)
                        .addValue("author_id", authorId)
                        .addValue("genre_id", genreId));
    }

    private long insertGenre(String name) {
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update("""
                        insert into genres (name)
                        values (:name)
                        """,
                new MapSqlParameterSource().addValue("name", name),
                keyHolder);
        return keyHolder.getKey().longValue();
    }
}
