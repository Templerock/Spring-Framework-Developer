package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.dto.GenreDto;
import ru.otus.spring.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public GenreDto add(String name) {
        return GenreDto.from(genreRepository.save(new Genre(0, name)));
    }

    public List<GenreDto> getAll() {
        return genreRepository.findAllByOrderByNameAsc().stream().map(GenreDto::from).toList();
    }

    public Optional<GenreDto> getById(long id) {
        return genreRepository.findById(id).map(GenreDto::from);
    }

    public Optional<GenreDto> update(long id, String name) {
        var existing = genreRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        var genre = existing.get();
        genre.setName(name);
        return Optional.of(GenreDto.from(genre));
    }

    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }
}
