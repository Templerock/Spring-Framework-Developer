package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.domain.Book;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.repository.AuthorRepository;
import ru.otus.spring.repository.BookRepository;
import ru.otus.spring.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    public Optional<BookDto> add(String title, long authorId, long genreId) {
        var author = authorRepository.findById(authorId);
        var genre = genreRepository.findById(genreId);
        if (author.isEmpty() || genre.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(BookDto.from(bookRepository.save(new Book(0, title, author.get(), genre.get()))));
    }

    public List<BookDto> getAll() {
        return bookRepository.findAllWithAuthorAndGenre().stream().map(BookDto::from).toList();
    }

    public Optional<BookDto> getById(long id) {
        return bookRepository.findWithAuthorAndGenreById(id).map(BookDto::from);
    }

    public Optional<BookDto> update(long id, String title, long authorId, long genreId) {
        var book = bookRepository.findWithAuthorAndGenreById(id);
        var author = authorRepository.findById(authorId);
        var genre = genreRepository.findById(genreId);
        if (book.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            return Optional.empty();
        }
        var existing = book.get();
        existing.setTitle(title);
        existing.setAuthor(author.get());
        existing.setGenre(genre.get());
        return Optional.of(BookDto.from(existing));
    }

    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }
}
