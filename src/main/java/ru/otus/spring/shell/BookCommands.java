package ru.otus.spring.shell;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.spring.domain.Book;
import ru.otus.spring.service.BookService;

import java.util.List;

@ShellComponent
public class BookCommands {

    private final BookService bookService;

    public BookCommands(BookService bookService) {
        this.bookService = bookService;
    }

    @ShellMethod(value = "Add a new book by existing author and genre ids", key = "book add")
    public String add(@ShellOption(value = "--title") String title,
                     @ShellOption(value = "--author-id") long authorId,
                     @ShellOption(value = "--genre-id") long genreId) {
        return bookService.add(title, authorId, genreId)
                .map(Book::toString)
                .map("Book added: "::concat)
                .orElse("Book cannot be added: author or genre not found");
    }

    @ShellMethod(value = "List all books", key = "book list")
    public List<Book> list() {
        return bookService.getAll();
    }

    @ShellMethod(value = "Get book by id", key = "book get")
    public String get(@ShellOption(value = "--id") long id) {
        return bookService.getById(id)
                .map(Object::toString)
                .orElse("Book with id=%d not found".formatted(id));
    }

    @ShellMethod(value = "Update book title/author/genre by id", key = "book update")
    public String update(@ShellOption(value = "--id") long id,
                        @ShellOption(value = "--title") String title,
                        @ShellOption(value = "--author-id") long authorId,
                        @ShellOption(value = "--genre-id") long genreId) {
        return bookService.update(id, title, authorId, genreId)
                .map(Book::toString)
                .map("Book updated: "::concat)
                .orElse("Book cannot be updated: book, author or genre not found");
    }

    @ShellMethod(value = "Delete book by id", key = "book delete")
    public String delete(@ShellOption(value = "--id") long id) {
        try {
            bookService.deleteById(id);
            return "Book with id=%d removed".formatted(id);
        } catch (DataIntegrityViolationException e) {
            return "Book with id=%d cannot be removed".formatted(id);
        }
    }
}
