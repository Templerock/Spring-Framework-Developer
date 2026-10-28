package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.dao.GenreDao;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({GenreService.class, GenreDao.class})
class GenreServiceTest {

    @Autowired
    private GenreService genreService;

    @Test
    void addReturnsCreatedGenreWithId() {
        var genre = genreService.add("Science Fiction");

        assertThat(genre.getId()).isPositive();
        assertThat(genreService.getById(genre.getId()))
                .get()
                .extracting(g -> g.getName())
                .isEqualTo("Science Fiction");
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(genreService.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateReturnsEmptyForUnknownId() {
        assertThat(genreService.update(Long.MAX_VALUE, "New")).isEmpty();
    }

    @Test
    void updateChangesExistingGenre() {
        var genre = genreService.add("Old");

        var updated = genreService.update(genre.getId(), "New");

        assertThat(updated)
                .get()
                .extracting(g -> g.getName())
                .isEqualTo("New");
    }

    @Test
    void deleteByIdRemovesGenre() {
        var genre = genreService.add("ToDelete");

        genreService.deleteById(genre.getId());

        assertThat(genreService.getById(genre.getId())).isEmpty();
    }
}
