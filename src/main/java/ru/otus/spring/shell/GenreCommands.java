package ru.otus.spring.shell;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.service.GenreService;

import java.util.List;

@ShellComponent
public class GenreCommands {

    private final GenreService genreService;

    public GenreCommands(GenreService genreService) {
        this.genreService = genreService;
    }

    @ShellMethod(value = "Add a new genre", key = "genre add")
    public String add(@ShellOption(value = "--name") String name) {
        return "Genre added: " + genreService.add(name);
    }

    @ShellMethod(value = "List all genres", key = "genre list")
    public List<Genre> list() {
        return genreService.getAll();
    }

    @ShellMethod(value = "Get genre by id", key = "genre get")
    public String get(@ShellOption(value = "--id") long id) {
        return genreService.getById(id)
                .map(Object::toString)
                .orElse("Genre with id=%d not found".formatted(id));
    }

    @ShellMethod(value = "Update genre name by id", key = "genre update")
    public String update(@ShellOption(value = "--id") long id,
                        @ShellOption(value = "--name") String name) {
        return genreService.update(id, name)
                .map(Genre::toString)
                .map("Genre updated: "::concat)
                .orElse("Genre with id=%d not found".formatted(id));
    }

    @ShellMethod(value = "Delete genre by id", key = "genre delete")
    public String delete(@ShellOption(value = "--id") long id) {
        try {
            genreService.deleteById(id);
            return "Genre with id=%d removed".formatted(id);
        } catch (DataIntegrityViolationException e) {
            return "Genre with id=%d cannot be removed: books reference it".formatted(id);
        }
    }
}
