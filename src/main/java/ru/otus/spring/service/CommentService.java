package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.domain.Comment;
import ru.otus.spring.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookService bookService;

    public CommentService(CommentRepository commentRepository, BookService bookService) {
        this.commentRepository = commentRepository;
        this.bookService = bookService;
    }

    public Optional<Comment> add(long bookId, String text) {
        var book = bookService.getById(bookId);
        if (book.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(commentRepository.save(new Comment(0, text, book.get())));
    }

    public List<Comment> getAll() {
        return commentRepository.findAllWithBook();
    }

    public Optional<Comment> getById(long id) {
        return commentRepository.findById(id);
    }

    public Optional<Comment> update(long id, String text) {
        var existing = commentRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        var comment = existing.get();
        comment.setText(text);
        return Optional.of(comment);
    }

    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
