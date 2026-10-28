package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dto.GenreDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GenreServiceTest {

    @Autowired
    private GenreService genreService;

    @Test
    void addReturnsCreatedGenreWithId() {
        var genre = genreService.add("Science Fiction");

        assertThat(genre.id()).isPositive();
        assertThat(genreService.getById(genre.id()))
                .get()
                .extracting(GenreDto::name)
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
        var genre = genreService.add("NewGenreName");

        var updated = genreService.update(genre.id(), "New");

        assertThat(updated)
                .get()
                .extracting(GenreDto::name)
                .isEqualTo("New");
    }

    @Test
    void deleteByIdRemovesGenre() {
        var genre = genreService.add("ToDeleteGenre");

        genreService.deleteById(genre.id());

        assertThat(genreService.getById(genre.id())).isEmpty();
    }
}
