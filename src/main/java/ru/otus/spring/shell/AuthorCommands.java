package ru.otus.spring.shell;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.spring.domain.Author;
import ru.otus.spring.service.AuthorService;

import java.util.List;

@ShellComponent
public class AuthorCommands {

    private final AuthorService authorService;

    public AuthorCommands(AuthorService authorService) {
        this.authorService = authorService;
    }

    @ShellMethod(value = "Add a new author", key = "author add")
    public String add(@ShellOption(value = "--first-name") String firstName,
                     @ShellOption(value = "--last-name") String lastName) {
        return "Author added: " + authorService.add(firstName, lastName);
    }

    @ShellMethod(value = "List all authors", key = "author list")
    public List<Author> list() {
        return authorService.getAll();
    }

    @ShellMethod(value = "Get author by id", key = "author get")
    public String get(@ShellOption(value = "--id") long id) {
        return authorService.getById(id)
                .map(Object::toString)
                .orElse("Author with id=%d not found".formatted(id));
    }

    @ShellMethod(value = "Update author first/last name by id", key = "author update")
    public String update(@ShellOption(value = "--id") long id,
                        @ShellOption(value = "--first-name") String firstName,
                        @ShellOption(value = "--last-name") String lastName) {
        return authorService.update(id, firstName, lastName)
                .map(Author::toString)
                .map("Author updated: "::concat)
                .orElse("Author with id=%d not found".formatted(id));
    }

    @ShellMethod(value = "Delete author by id", key = "author delete")
    public String delete(@ShellOption(value = "--id") long id) {
        try {
            authorService.deleteById(id);
            return "Author with id=%d removed".formatted(id);
        } catch (DataIntegrityViolationException e) {
            return "Author with id=%d cannot be removed: books reference it".formatted(id);
        }
    }
}
