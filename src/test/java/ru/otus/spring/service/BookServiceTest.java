package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.dao.AuthorDao;
import ru.otus.spring.dao.BookDao;
import ru.otus.spring.dao.GenreDao;
import ru.otus.spring.domain.Book;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({BookService.class, AuthorService.class, GenreService.class, BookDao.class, AuthorDao.class, GenreDao.class})
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    @Test
    void addReturnsEmptyWhenAuthorOrGenreNotFound() {
        long genreId = genreService.add("Epic").getId();

        assertThat(bookService.add("Book", Long.MAX_VALUE, genreId)).isEmpty();
        assertThat(bookService.add("Book", 1L, Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void addCreatesBookWithAuthorAndGenre() {
        long authorId = authorService.add("Julian", "Barnes").getId();
        long genreId = genreService.add("Epic").getId();

        var saved = bookService.add("Flaubert's Parrot", authorId, genreId);

        assertThat(saved)
                .get()
                .satisfies(book -> {
                    assertThat(book.getAuthor().getId()).isEqualTo(authorId);
                    assertThat(book.getGenre().getName()).isEqualTo("Epic");
                });
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(bookService.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateReturnsEmptyWhenBookAuthorOrGenreNotFound() {
        long authorId = authorService.add("A", "B").getId();
        long genreId = genreService.add("G").getId();
        long bookId = bookService.add("Book", authorId, genreId).get().getId();

        assertThat(bookService.update(bookId, "X", Long.MAX_VALUE, genreId)).isEmpty();
        assertThat(bookService.update(Long.MAX_VALUE, "X", authorId, genreId)).isEmpty();
    }

    @Test
    void updateChangesExistingBook() {
        long authorId1 = authorService.add("One", "Author").getId();
        long authorId2 = authorService.add("Two", "Author").getId();
        long genreId = genreService.add("Thriller").getId();
        long bookId = bookService.add("Old", authorId1, genreId).get().getId();

        var updated = bookService.update(bookId, "New", authorId2, genreId);

        assertThat(updated).isPresent();
        Book book = updated.orElseThrow();
        assertThat(book.getTitle()).isEqualTo("New");
        assertThat(book.getAuthor().getId()).isEqualTo(authorId2);
    }

    @Test
    void deleteByIdRemovesBook() {
        long authorId = authorService.add("To", "Del").getId();
        long genreId = genreService.add("Mystery").getId();
        long bookId = bookService.add("Doomed", authorId, genreId).get().getId();

        bookService.deleteById(bookId);

        assertThat(bookService.getById(bookId)).isEmpty();
    }
}
