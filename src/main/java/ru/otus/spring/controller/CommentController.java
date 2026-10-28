package ru.otus.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.spring.dto.CommentDto;
import ru.otus.spring.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> findAll() {
        return commentService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> findById(@PathVariable long id) {
        return commentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CommentDto> create(@RequestBody CommentDto comment) {
        return commentService.add(comment.bookId(), comment.text())
                .map(created -> ResponseEntity.<CommentDto>status(HttpStatus.CREATED).body(created))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable long id, @RequestBody CommentDto comment) {
        return commentService.update(id, comment.text())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (commentService.getById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
