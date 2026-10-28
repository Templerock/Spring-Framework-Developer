package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import ru.otus.spring.dao.GenreDao;
import ru.otus.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {

    private final GenreDao genreDao;

    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Genre add(String name) {
        var genre = new Genre(0, name);
        genreDao.insert(genre);
        return genreDao.getById(genre.getId()).orElseThrow();
    }

    public List<Genre> getAll() {
        return genreDao.getAll();
    }

    public Optional<Genre> getById(long id) {
        return genreDao.getById(id);
    }

    public Optional<Genre> update(long id, String name) {
        if (genreDao.getById(id).isEmpty()) {
            return Optional.empty();
        }
        genreDao.update(new Genre(id, name));
        return genreDao.getById(id);
    }

    public void deleteById(long id) {
        genreDao.deleteById(id);
    }
}
