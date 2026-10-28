package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dao.BookDao;
import ru.otus.spring.domain.Book;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
        var author = authorService.getById(authorId);
        var genre = genreService.getById(genreId);
        if (author.isEmpty() || genre.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(bookDao.save(new Book(0, title, author.get(), genre.get())));
    }

    public List<Book> getAll() {
        return bookDao.getAll();
    }

    public Optional<Book> getById(long id) {
        return bookDao.getById(id);
    }

    public Optional<Book> update(long id, String title, long authorId, long genreId) {
        var book = bookDao.getById(id);
        var author = authorService.getById(authorId);
        var genre = genreService.getById(genreId);
        if (book.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            return Optional.empty();
        }
        var existing = book.get();
        existing.setTitle(title);
        existing.setAuthor(author.get());
        existing.setGenre(genre.get());
        return Optional.of(bookDao.save(existing));
    }

    public void deleteById(long id) {
        bookDao.getById(id).ifPresent(bookDao::delete);
    }
}
