package ru.otus.spring.shell;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.spring.domain.Comment;
import ru.otus.spring.service.CommentService;

import java.util.List;

@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    public CommentCommands(CommentService commentService) {
        this.commentService = commentService;
    }

    @ShellMethod(value = "Add a new comment to an existing book", key = "comment add")
    public String add(@ShellOption(value = "--book-id") long bookId,
                     @ShellOption(value = "--text") String text) {
        return commentService.add(bookId, text)
                .map(Comment::toString)
                .map("Comment added: "::concat)
                .orElse("Comment cannot be added: book not found");
    }

    @ShellMethod(value = "List all comments", key = "comment list")
    public List<Comment> list() {
        return commentService.getAll();
    }

    @ShellMethod(value = "Get comment by id", key = "comment get")
    public String get(@ShellOption(value = "--id") long id) {
        return commentService.getById(id)
                .map(Object::toString)
                .orElse("Comment with id=%d not found".formatted(id));
    }

    @ShellMethod(value = "Update comment text by id", key = "comment update")
    public String update(@ShellOption(value = "--id") long id,
                        @ShellOption(value = "--text") String text) {
        return commentService.update(id, text)
                .map(Comment::toString)
                .map("Comment updated: "::concat)
                .orElse("Comment with id=%d not found".formatted(id));
    }

    @ShellMethod(value = "Delete comment by id", key = "comment delete")
    public String delete(@ShellOption(value = "--id") long id) {
        try {
            commentService.deleteById(id);
            return "Comment with id=%d removed".formatted(id);
        } catch (DataIntegrityViolationException e) {
            return "Comment with id=%d cannot be removed".formatted(id);
        }
    }
}
