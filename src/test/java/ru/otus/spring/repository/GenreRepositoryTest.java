package ru.otus.spring.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager em;

    @Test
    void savedGenreCanBeReadById() {
        var genre = genreRepository.save(new Genre(0, "Science Fiction"));

        assertThat(genre.getId()).isPositive();
        assertThat(genreRepository.findById(genre.getId()))
                .get()
                .extracting(Genre::getName)
                .isEqualTo("Science Fiction");
    }

    @Test
    void findAllByOrderByNameAscReturnsSorted() {
        genreRepository.save(new Genre(0, "Novella"));

        assertThat(genreRepository.findAllByOrderByNameAsc())
                .extracting(Genre::getName)
                .contains("Novella")
                .isSorted();
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(genreRepository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesName() {
        var genre = genreRepository.save(new Genre(0, "Satire"));

        genreRepository.save(new Genre(genre.getId(), "New"));

        assertThat(genreRepository.findById(genre.getId()))
                .get()
                .extracting(Genre::getName)
                .isEqualTo("New");
    }

    @Test
    void deleteRemovesGenre() {
        var genre = genreRepository.save(new Genre(0, "ToDelete"));

        genreRepository.delete(genre);

        assertThat(genreRepository.findById(genre.getId())).isEmpty();
    }

    @Test
    void countMatchesInsertions() {
        long before = genreRepository.count();

        genreRepository.save(new Genre(0, "ExtraGenre"));

        assertThat(genreRepository.count()).isEqualTo(before + 1);
    }

    @Test
    void deleteGenreReferencedByBookThrows() {
        var author = authorRepository.save(new Author(0, "Referenced", "Author"));
        var genre = genreRepository.save(new Genre(0, "ReferencedGenre"));
        bookRepository.save(new Book(0, "RefBook", author, genre));
        em.flush();

        assertThatThrownBy(() -> {
            genreRepository.delete(genre);
            em.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }
}
