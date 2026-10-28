package ru.otus.spring.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(BookDao.class)
class BookDaoTest {

    @Autowired
    private BookDao bookDao;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Test
    void insertedBookCanBeReadByIdWithAuthorAndGenre() {
        long authorId = insertAuthor("Julian", "Barnes");
        long genreId = insertGenre("Picaresque");

        long id = bookDao.insert(new Book(0, "Flaubert's Parrot", new Author(authorId), new Genre(genreId)));

        assertThat(bookDao.getById(id))
                .get()
                .satisfies(book -> {
                    assertThat(book.getTitle()).isEqualTo("Flaubert's Parrot");
                    assertThat(book.getAuthor().getId()).isEqualTo(authorId);
                    assertThat(book.getAuthor().getLastName()).isEqualTo("Barnes");
                    assertThat(book.getGenre().getId()).isEqualTo(genreId);
                    assertThat(book.getGenre().getName()).isEqualTo("Picaresque");
                });
    }

    @Test
    void getAllReturnsBooksWithAuthorAndGenre() {
        var books = bookDao.getAll();

        assertThat(books).isNotEmpty()
                .allSatisfy(book -> {
                    assertThat(book.getTitle()).isNotBlank();
                    assertThat(book.getAuthor()).isNotNull();
                    assertThat(book.getAuthor().getLastName()).isNotBlank();
                    assertThat(book.getGenre()).isNotNull();
                    assertThat(book.getGenre().getName()).isNotBlank();
                });
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(bookDao.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesTitleAuthorAndGenre() {
        long authorId1 = insertAuthor("One", "Author");
        long authorId2 = insertAuthor("Two", "Author");
        long genreId = insertGenre("Saga");
        long id = bookDao.insert(new Book(0, "Old title", new Author(authorId1), new Genre(genreId)));

        bookDao.update(new Book(id, "New title", new Author(authorId2), new Genre(genreId)));

        assertThat(bookDao.getById(id))
                .get()
                .satisfies(book -> {
                    assertThat(book.getTitle()).isEqualTo("New title");
                    assertThat(book.getAuthor().getId()).isEqualTo(authorId2);
                });
    }

    @Test
    void deleteByIdRemovesBook() {
        long authorId = insertAuthor("To", "Delete");
        long genreId = insertGenre("Poetry");
        long id = bookDao.insert(new Book(0, "Doomed", new Author(authorId), new Genre(genreId)));

        bookDao.deleteById(id);

        assertThat(bookDao.getById(id)).isEmpty();
    }

    @Test
    void countMatchesInsertions() {
        long authorId = insertAuthor("Count", "Author");
        long genreId = insertGenre("Drama");        long before = bookDao.count();

        bookDao.insert(new Book(0, "Counted", new Author(authorId), new Genre(genreId)));

        assertThat(bookDao.count()).isEqualTo(before + 1);
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
