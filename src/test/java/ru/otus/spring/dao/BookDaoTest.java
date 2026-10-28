package ru.otus.spring.dao;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Comment;
import ru.otus.spring.domain.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BookDao.class, AuthorDao.class, GenreDao.class, CommentDao.class})
class BookDaoTest {

    @Autowired
    private BookDao bookDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private GenreDao genreDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private EntityManager em;

    @Test
    void savedBookCanBeReadByIdWithAuthorAndGenre() {
        var author = authorDao.save(new Author(0, "Julian", "Barnes"));
        var genre = genreDao.save(new Genre(0, "Satire"));

        var book = bookDao.save(new Book(0, "Flaubert's Parrot", author, genre));

        assertThat(book.getId()).isPositive();
        assertThat(bookDao.getById(book.getId()))
                .get()
                .satisfies(saved -> {
                    assertThat(saved.getTitle()).isEqualTo("Flaubert's Parrot");
                    assertThat(saved.getAuthor().getLastName()).isEqualTo("Barnes");
                    assertThat(saved.getGenre().getName()).isEqualTo("Satire");
                });
    }

    @Test
    void getAllReturnsBooksWithAuthorAndGenre() {
        assertThat(bookDao.getAll()).isNotEmpty()
                .allSatisfy(book -> {
                    assertThat(book.getTitle()).isNotBlank();
                    assertThat(book.getAuthor()).isNotNull();
                    assertThat(book.getGenre()).isNotNull();
                });
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(bookDao.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesBook() {
        var author1 = authorDao.save(new Author(0, "One", "Author"));
        var author2 = authorDao.save(new Author(0, "Two", "Author"));
        var genre = genreDao.save(new Genre(0, "Thriller"));
        var book = bookDao.save(new Book(0, "Old", author1, genre));

        var updated = bookDao.save(new Book(book.getId(), "New", author2, genre));

        assertThat(updated.getTitle()).isEqualTo("New");
        assertThat(bookDao.getById(book.getId()))
                .get()
                .extracting(Book::getTitle)
                .isEqualTo("New");
    }

    @Test
    void deleteRemovesBook() {
        var author = authorDao.save(new Author(0, "Grant", "McAllister"));
        var genre = genreDao.save(new Genre(0, "Crime"));
        var book = bookDao.save(new Book(0, "Doomed", author, genre));

        bookDao.delete(book);

        assertThat(bookDao.getById(book.getId())).isEmpty();
    }

    @Test
    void deleteRemovesLinkedComments() {
        var author = authorDao.save(new Author(0, "Neil", "Gaiman"));
        var genre = genreDao.save(new Genre(0, "FantasyStory"));
        var book = bookDao.save(new Book(0, "Stardust", author, genre));
        var comment = commentDao.save(new Comment(0, "A fairy tale for adults", book));
        book.getComments().add(comment);
        em.flush();

        bookDao.delete(book);
        em.flush();

        assertThat(bookDao.getById(book.getId())).isEmpty();
        assertThat(commentDao.getById(comment.getId())).isEmpty();
    }
}
