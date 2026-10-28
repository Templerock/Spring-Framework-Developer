package ru.otus.spring.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Comment;
import ru.otus.spring.domain.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager em;

    private Book newBook(String title, Genre genre) {
        return bookRepository.save(new Book(0, title,
                authorRepository.save(new Author(0, "Julian", "Barnes")), genre));
    }

    @Test
    void findWithAuthorAndGenreByIdReturnsBookWithAuthorAndGenre() {
        var genre = genreRepository.save(new Genre(0, "Novella"));
        var book = newBook("Flaubert's Parrot", genre);

        assertThat(bookRepository.findWithAuthorAndGenreById(book.getId()))
                .get()
                .satisfies(saved -> {
                    assertThat(saved.getTitle()).isEqualTo("Flaubert's Parrot");
                    assertThat(saved.getAuthor().getLastName()).isEqualTo("Barnes");
                    assertThat(saved.getGenre().getName()).isEqualTo("Novella");
                });
    }

    @Test
    void findAllWithAuthorAndGenreReturnsSeededBooks() {
        assertThat(bookRepository.findAllWithAuthorAndGenre()).isNotEmpty()
                .allSatisfy(book -> {
                    assertThat(book.getTitle()).isNotBlank();
                    assertThat(book.getAuthor()).isNotNull();
                    assertThat(book.getGenre()).isNotNull();
                });
    }

    @Test
    void findWithAuthorAndGenreByIdReturnsEmptyForUnknownId() {
        assertThat(bookRepository.findWithAuthorAndGenreById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertThat(bookRepository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesBook() {
        var author1 = authorRepository.save(new Author(0, "One", "Author"));
        var author2 = authorRepository.save(new Author(0, "Two", "Author"));
        var genre = genreRepository.save(new Genre(0, "Thriller"));
        var book = bookRepository.save(new Book(0, "Old", author1, genre));

        var existing = bookRepository.findWithAuthorAndGenreById(book.getId()).orElseThrow();
        existing.setTitle("New");
        existing.setAuthor(author2);

        assertThat(bookRepository.findWithAuthorAndGenreById(book.getId()))
                .get()
                .extracting(Book::getTitle, b -> b.getAuthor().getId())
                .containsExactly("New", author2.getId());
    }

    @Test
    void deleteRemovesBook() {
        var genre = genreRepository.save(new Genre(0, "Crime"));
        var book = newBook("Doomed", genre);

        bookRepository.delete(book);

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }

    @Test
    void deleteRemovesLinkedComments() {
        var author = authorRepository.save(new Author(0, "Neil", "Gaiman"));
        var genre = genreRepository.save(new Genre(0, "FantasyStory"));
        var book = bookRepository.save(new Book(0, "Stardust", author, genre));
        var comment = commentRepository.save(new Comment(0, "A fairy tale for adults", book));
        book.getComments().add(comment);
        em.flush();

        bookRepository.delete(book);
        em.flush();

        assertThat(bookRepository.findById(book.getId())).isEmpty();
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }
}
