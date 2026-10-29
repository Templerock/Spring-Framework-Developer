package ru.otus.spring.dto;

import ru.otus.spring.domain.Comment;

public record CommentDto(Long id, String text, Long bookId) {

    public static CommentDto from(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getBook().getId());
    }
}
