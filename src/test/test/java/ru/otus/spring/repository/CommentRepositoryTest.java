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
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private EntityManager em;

    private Book newBook(String title) {
        return bookRepository.save(new Book(0, title,
                authorRepository.save(new Author(0, "Author", "Writer")),
                genreRepository.save(new Genre(0, "Tale"))));
    }

    @Test
    void savedCommentCanBeReadById() {
        var book = newBook("Book One");

        var comment = commentRepository.save(new Comment(0, "Great book", book));

        assertThat(comment.getId()).isPositive();
        assertThat(commentRepository.findById(comment.getId()))
                .get()
                .satisfies(saved -> {
                    assertThat(saved.getText()).isEqualTo("Great book");
                    assertThat(saved.getBook().getTitle()).isEqualTo("Book One");
                });
    }

    @Test
    void findAllWithBookReturnsSeededAndNewComments() {
        assertThat(commentRepository.findAllWithBook()).isNotEmpty();

        var comment = commentRepository.save(new Comment(0, "New comment", newBook("Book Two")));

        assertThat(commentRepository.findAllWithBook())
                .extracting(Comment::getId)
                .contains(comment.getId());
    }

    @Test
    void findAllByBookIdReturnsOnlyThatBookComments() {
        var book = newBook("Book Three");

        Long bookId = book.getId();
        commentRepository.save(new Comment(0, "First", book));
        em.flush();
        commentRepository.save(new Comment(0, "Second", book));
        em.flush();

        assertThat(commentRepository.findAllByBookId(bookId))
                .extracting(Comment::getText)
                .containsExactlyInAnyOrder("First", "Second");
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertThat(commentRepository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesText() {
        var comment = commentRepository.save(new Comment(0, "Old", newBook("Book Four")));

        commentRepository.save(new Comment(comment.getId(), "New", comment.getBook()));

        assertThat(commentRepository.findById(comment.getId()))
                .get()
                .extracting(Comment::getText)
                .isEqualTo("New");
    }

    @Test
    void deleteRemovesComment() {
        var comment = commentRepository.save(new Comment(0, "ToDelete", newBook("Book Five")));

        commentRepository.delete(comment);

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }
}
