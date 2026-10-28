package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dao.AuthorDao;
import ru.otus.spring.domain.Author;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthorService {

    private final AuthorDao authorDao;

    public AuthorService(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public Author add(String firstName, String lastName) {
        return authorDao.save(new Author(0, firstName, lastName));
    }

    public List<Author> getAll() {
        return authorDao.getAll();
    }

    public Optional<Author> getById(long id) {
        return authorDao.getById(id);
    }

    public Optional<Author> update(long id, String firstName, String lastName) {
        if (authorDao.getById(id).isEmpty()) {
            return Optional.empty();
        }
        var author = authorDao.getById(id).orElseThrow();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        return Optional.of(authorDao.save(author));
    }

    public void deleteById(long id) {
        authorDao.getById(id).ifPresent(authorDao::delete);
    }
}
