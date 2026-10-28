package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
                authorService.add("Arts", "Writer").getId(),
                genreService.add("Saga").getId()).get().getId();
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
                    assertThat(comment.getText()).isEqualTo("Worth reading");
                    assertThat(comment.getBook().getTitle()).isEqualTo("Commented Book");
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
        long commentId = commentService.add(newBookId(), "Old").get().getId();

        var updated = commentService.update(commentId, "New");

        assertThat(updated)
                .get()
                .satisfies(comment -> {
                    assertThat(comment.getText()).isEqualTo("New");
                });
    }

    @Test
    void deleteByIdRemovesComment() {
        long commentId = commentService.add(newBookId(), "ToDelete").get().getId();

        commentService.deleteById(commentId);

        assertThat(commentService.getById(commentId)).isEmpty();
    }
}
