package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.domain.Author;
import ru.otus.spring.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author add(String firstName, String lastName) {
        return authorRepository.save(new Author(0, firstName, lastName));
    }

    public List<Author> getAll() {
        return authorRepository.findAllByOrderByLastNameAscFirstNameAsc();
    }

    public Optional<Author> getById(long id) {
        return authorRepository.findById(id);
    }

    public Optional<Author> update(long id, String firstName, String lastName) {
        var existing = authorRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        var author = existing.get();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        return Optional.of(author);
    }

    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }
}
