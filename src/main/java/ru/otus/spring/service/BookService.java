package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import ru.otus.spring.dao.BookDao;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookDao bookDao;
    private final AuthorService authorService;
    private final GenreService genreService;

    public BookService(BookDao bookDao, AuthorService authorService, GenreService genreService) {
        this.bookDao = bookDao;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    public Optional<Book> add(String title, long authorId, long genreId) {
        if (authorService.getById(authorId).isEmpty() || genreService.getById(genreId).isEmpty()) {
            return Optional.empty();
        }
        var book = new Book(0, title, new Author(authorId), new Genre(genreId));
        bookDao.insert(book);
        return bookDao.getById(book.getId());
    }

    public List<Book> getAll() {
        return bookDao.getAll();
    }

    public Optional<Book> getById(long id) {
        return bookDao.getById(id);
    }

    public Optional<Book> update(long id, String title, long authorId, long genreId) {
        if (bookDao.getById(id).isEmpty()
                || authorService.getById(authorId).isEmpty()
                || genreService.getById(genreId).isEmpty()) {
            return Optional.empty();
        }
        bookDao.update(new Book(id, title, new Author(authorId), new Genre(genreId)));
        return bookDao.getById(id);
    }

    public void deleteById(long id) {
        bookDao.deleteById(id);
    }
}
