package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.domain.Author;
import ru.otus.spring.dto.AuthorDto;
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

    public AuthorDto add(String firstName, String lastName) {
        return AuthorDto.from(authorRepository.save(new Author(0, firstName, lastName)));
    }

    public List<AuthorDto> getAll() {
        return authorRepository.findAllByOrderByLastNameAscFirstNameAsc().stream().map(AuthorDto::from).toList();
    }

    public Optional<AuthorDto> getById(long id) {
        return authorRepository.findById(id).map(AuthorDto::from);
    }

    public Optional<AuthorDto> update(long id, String firstName, String lastName) {
        var existing = authorRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        var author = existing.get();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        return Optional.of(AuthorDto.from(author));
    }

    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }
}
