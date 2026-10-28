package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.dto.CommentDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    private long newBookId() {
        return bookService.add("Commented Book",
                        authorService.add("Arts", "Writer").id(),
                        genreService.add("Saga").id())
                .map(BookDto::id).orElseThrow();
    }

    @Test
    void addReturnsEmptyWhenBookNotFound() {
        assertThat(commentService.add(Long.MAX_VALUE, "Text")).isEmpty();
    }

    @Test
    void addCreatesCommentForBook() {
        long bookId = newBookId();

        var saved = commentService.add(bookId, "Worth reading");

        assertThat(saved)
                .get()
                .satisfies(comment -> {
                    assertThat(comment.text()).isEqualTo("Worth reading");
                    assertThat(comment.bookId()).isEqualTo(bookId);
                });
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(commentService.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateReturnsEmptyForUnknownId() {
        assertThat(commentService.update(Long.MAX_VALUE, "New")).isEmpty();
    }

    @Test
    void updateChangesText() {
        long commentId = commentService.add(newBookId(), "Old").get().id();

        var updated = commentService.update(commentId, "New");

        assertThat(updated)
                .get()
                .extracting(CommentDto::text)
                .isEqualTo("New");
    }

    @Test
    void deleteByIdRemovesComment() {
        long commentId = commentService.add(newBookId(), "ToDelete").get().id();

        commentService.deleteById(commentId);

        assertThat(commentService.getById(commentId)).isEmpty();
    }
}
