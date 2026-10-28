package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dao.GenreDao;
import ru.otus.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GenreService {

    private final GenreDao genreDao;

    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Genre add(String name) {
        return genreDao.save(new Genre(0, name));
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
        var genre = genreDao.getById(id).orElseThrow();
        genre.setName(name);
        return Optional.of(genreDao.save(genre));
    }

    public void deleteById(long id) {
        genreDao.getById(id).ifPresent(genreDao::delete);
    }
}
