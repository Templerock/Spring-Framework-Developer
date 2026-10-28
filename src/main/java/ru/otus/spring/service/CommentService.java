package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.domain.Comment;
import ru.otus.spring.dto.CommentDto;
import ru.otus.spring.repository.BookRepository;
import ru.otus.spring.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    public CommentService(CommentRepository commentRepository, BookRepository bookRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
    }

    public Optional<CommentDto> add(long bookId, String text) {
        var book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(CommentDto.from(commentRepository.save(new Comment(0, text, book.get()))));
    }

    public List<CommentDto> getAll() {
        return commentRepository.findAllWithBook().stream().map(CommentDto::from).toList();
    }

    public Optional<CommentDto> getById(long id) {
        return commentRepository.findById(id).map(CommentDto::from);
    }

    public Optional<CommentDto> update(long id, String text) {
        var existing = commentRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        var comment = existing.get();
        comment.setText(text);
        return Optional.of(CommentDto.from(comment));
    }

    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
