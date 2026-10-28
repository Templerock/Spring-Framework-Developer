package ru.otus.spring.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.otus.spring.domain.Genre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import(GenreDao.class)
class GenreDaoTest {

    @Autowired
    private GenreDao genreDao;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Test
    void insertedGenreCanBeReadById() {
        var genre = new Genre(0, "Science Fiction");

        long id = genreDao.insert(genre);

        assertThat(id).isPositive();
        assertThat(genreDao.getById(id))
                .get()
                .extracting(Genre::getName)
                .isEqualTo("Science Fiction");
    }

    @Test
    void getAllReturnsSeededAndNewlyInsertedGenres() {
        assertThat(genreDao.getAll()).isNotEmpty();

        long id = genreDao.insert(new Genre(0, "Horror"));

        assertThat(genreDao.getAll())
                .extracting(Genre::getId)
                .contains(id);
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(genreDao.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesName() {
        long id = genreDao.insert(new Genre(0, "Old"));

        genreDao.update(new Genre(id, "New"));

        assertThat(genreDao.getById(id))
                .get()
                .extracting(Genre::getName)
                .isEqualTo("New");
    }

    @Test
    void deleteByIdRemovesGenre() {
        long id = genreDao.insert(new Genre(0, "ToDelete"));

        genreDao.deleteById(id);

        assertThat(genreDao.getById(id)).isEmpty();
    }

    @Test
    void deleteGenreReferencedByBookThrows() {
        long genreId = genreDao.insert(new Genre(0, "RefGenre"));
        long authorId = insertAuthor("Ref", "Author");
        jdbc.update("""
                        insert into books (title, author_id, genre_id)
                        values (:title, :author_id, :genre_id)
                        """,
                new MapSqlParameterSource()
                        .addValue("title", "Ref Book")
                        .addValue("author_id", authorId)
                        .addValue("genre_id", genreId));

        assertThatThrownBy(() -> genreDao.deleteById(genreId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deletingUnknownGenreIsNoOp() {
        assertThatCode(() -> genreDao.deleteById(Long.MAX_VALUE)).doesNotThrowAnyException();
    }

    @Test
    void countMatchesInsertions() {
        long before = genreDao.count();

        genreDao.insert(new Genre(0, "ExtraGenre"));

        assertThat(genreDao.count()).isEqualTo(before + 1);
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
}
