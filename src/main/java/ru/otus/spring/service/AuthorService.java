package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import ru.otus.spring.dao.AuthorDao;
import ru.otus.spring.domain.Author;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorDao authorDao;

    public AuthorService(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public Author add(String firstName, String lastName) {
        var author = new Author(0, firstName, lastName);
        authorDao.insert(author);
        return authorDao.getById(author.getId()).orElseThrow();
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
        authorDao.update(new Author(id, firstName, lastName));
        return authorDao.getById(id);
    }

    public void deleteById(long id) {
        authorDao.deleteById(id);
    }
}
