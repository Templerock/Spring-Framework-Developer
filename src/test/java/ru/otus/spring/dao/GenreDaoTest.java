package ru.otus.spring.dao;

import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(GenreDao.class)
class GenreDaoTest {

    @Autowired
    private GenreDao genreDao;

    @Autowired
    private EntityManager em;

    @Test
    void savedGenreCanBeReadById() {
        var genre = genreDao.save(new Genre(0, "Science Fiction"));

        assertThat(genre.getId()).isPositive();
        assertThat(genreDao.getById(genre.getId()))
                .get()
                .extracting(Genre::getName)
                .isEqualTo("Science Fiction");
    }

    @Test
    void getAllReturnsSeededAndNewlySavedGenres() {
        assertThat(genreDao.getAll()).isNotEmpty();

        var genre = genreDao.save(new Genre(0, "Horror"));

        assertThat(genreDao.getAll())
                .extracting(Genre::getId)
                .contains(genre.getId());
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(genreDao.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesName() {
        var genre = genreDao.save(new Genre(0, "Satire"));

        genreDao.save(new Genre(genre.getId(), "New"));

        assertThat(genreDao.getById(genre.getId()))
                .get()
                .extracting(Genre::getName)
                .isEqualTo("New");
    }

    @Test
    void deleteRemovesGenre() {
        var genre = genreDao.save(new Genre(0, "ToDelete"));

        genreDao.delete(genre);

        assertThat(genreDao.getById(genre.getId())).isEmpty();
    }

    @Test
    void countMatchesInsertions() {
        long before = genreDao.count();

        genreDao.save(new Genre(0, "ExtraGenre"));

        assertThat(genreDao.count()).isEqualTo(before + 1);
    }

    @Test
    void deleteGenreReferencedByBookThrows() {
        var author = new Author(0, "Referenced", "Author");
        em.persist(author);
        var genre = genreDao.save(new Genre(0, "ReferencedGenre"));
        em.persist(new Book(0, "RefBook", author, genre));
        em.flush();

        assertThatThrownBy(() -> {
            genreDao.delete(genre);
            em.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }
}
