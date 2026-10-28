package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    @Test
    void addReturnsEmptyWhenAuthorOrGenreNotFound() {
        long genreId = genreService.add("Epic").id();

        assertThat(bookService.add("Book", Long.MAX_VALUE, genreId)).isEmpty();
        assertThat(bookService.add("Book", 1L, Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void addCreatesBookWithAuthorAndGenre() {
        long authorId = authorService.add("Julian", "Barnes").id();
        long genreId = genreService.add("Epic").id();

        var saved = bookService.add("Flaubert's Parrot", authorId, genreId);

        assertThat(saved)
                .get()
                .satisfies(book -> {
                    assertThat(book.authorId()).isEqualTo(authorId);
                    assertThat(book.genreId()).isEqualTo(genreId);
                });
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(bookService.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateReturnsEmptyWhenBookAuthorOrGenreNotFound() {
        long authorId = authorService.add("A", "B").id();
        long genreId = genreService.add("G").id();
        long bookId = bookService.add("Book", authorId, genreId).get().id();

        assertThat(bookService.update(bookId, "X", Long.MAX_VALUE, genreId)).isEmpty();
        assertThat(bookService.update(Long.MAX_VALUE, "X", authorId, genreId)).isEmpty();
    }

    @Test
    void updateChangesExistingBook() {
        long authorId1 = authorService.add("One", "Author").id();
        long authorId2 = authorService.add("Two", "Author").id();
        long genreId = genreService.add("Thriller").id();
        long bookId = bookService.add("Old", authorId1, genreId).get().id();

        var updated = bookService.update(bookId, "New", authorId2, genreId);

        assertThat(updated)
                .get()
                .satisfies(book -> {
                    assertThat(book.title()).isEqualTo("New");
                    assertThat(book.authorId()).isEqualTo(authorId2);
                });
    }

    @Test
    void deleteByIdRemovesBook() {
        long genreId = genreService.add("Drama").id();
        long authorId = authorService.add("To", "Del").id();
        long bookId = bookService.add("Doomed", authorId, genreId).get().id();

        bookService.deleteById(bookId);

        assertThat(bookService.getById(bookId)).isEmpty();
    }
}
