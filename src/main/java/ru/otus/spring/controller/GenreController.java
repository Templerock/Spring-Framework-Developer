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
import ru.otus.spring.dto.GenreDto;
import ru.otus.spring.service.GenreService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreDto> findAll() {
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> findById(@PathVariable long id) {
        return genreService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GenreDto> create(@RequestBody GenreDto genre) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.add(genre.name()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> update(@PathVariable long id, @RequestBody GenreDto genre) {
        return genreService.update(id, genre.name())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (genreService.getById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
