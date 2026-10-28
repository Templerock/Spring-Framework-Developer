package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dao.CommentDao;
import ru.otus.spring.domain.Comment;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {

    private final CommentDao commentDao;
    private final BookService bookService;

    public CommentService(CommentDao commentDao, BookService bookService) {
        this.commentDao = commentDao;
        this.bookService = bookService;
    }

    public Optional<Comment> add(long bookId, String text) {
        var book = bookService.getById(bookId);
        if (book.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(commentDao.save(new Comment(0, text, book.get())));
    }

    public List<Comment> getAll() {
        return commentDao.getAll();
    }

    public Optional<Comment> getById(long id) {
        return commentDao.getById(id);
    }

    public Optional<Comment> update(long id, String text) {
        if (commentDao.getById(id).isEmpty()) {
            return Optional.empty();
        }
        var comment = commentDao.getById(id).orElseThrow();
        comment.setText(text);
        return Optional.of(commentDao.save(comment));
    }

    public void deleteById(long id) {
        commentDao.getById(id).ifPresent(commentDao::delete);
    }
}
