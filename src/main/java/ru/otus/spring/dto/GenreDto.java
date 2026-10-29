package ru.otus.spring.dto;

import ru.otus.spring.domain.Genre;

public record GenreDto(Long id, String name) {

    public static GenreDto from(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
