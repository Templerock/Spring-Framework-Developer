package ru.otus.spring.dao;

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
@Import({CommentDao.class, BookDao.class, AuthorDao.class, GenreDao.class})
class CommentDaoTest {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private GenreDao genreDao;

    private Book newBook(String title) {
        return bookDao.save(new Book(0, title,
                authorDao.save(new Author(0, "Author", "Name")),
                genreDao.save(new Genre(0, "Genre"))));
    }

    @Test
    void savedCommentCanBeReadById() {
        var book = newBook("Book");

        var comment = commentDao.save(new Comment(0, "Great book", book));

        assertThat(comment.getId()).isPositive();
        assertThat(commentDao.getById(comment.getId()))
                .get()
                .satisfies(saved -> {
                    assertThat(saved.getText()).isEqualTo("Great book");
                    assertThat(saved.getBook().getTitle()).isEqualTo("Book");
                });
    }

    @Test
    void getAllReturnsSeededAndNewComments() {
        assertThat(commentDao.getAll()).isNotEmpty();

        var comment = commentDao.save(new Comment(0, "New comment", newBook("Another Book")));

        assertThat(commentDao.getAll())
                .extracting(Comment::getId)
                .contains(comment.getId());
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(commentDao.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesText() {
        var comment = commentDao.save(new Comment(0, "Old", newBook("Book Three")));

        commentDao.save(new Comment(comment.getId(), "New", comment.getBook()));

        assertThat(commentDao.getById(comment.getId()))
                .get()
                .extracting(Comment::getText)
                .isEqualTo("New");
    }

    @Test
    void deleteRemovesComment() {
        var comment = commentDao.save(new Comment(0, "ToDelete", newBook("Book Four")));

        commentDao.delete(comment);

        assertThat(commentDao.getById(comment.getId())).isEmpty();
    }

    @Test
    void countMatchesInsertions() {
        long before = commentDao.count();

        commentDao.save(new Comment(0, "Another", newBook("Book Five")));

        assertThat(commentDao.count()).isEqualTo(before + 1);
    }
}
