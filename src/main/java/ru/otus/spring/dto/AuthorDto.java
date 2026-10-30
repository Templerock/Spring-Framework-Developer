package ru.otus.spring.dto;

import ru.otus.spring.domain.Author;

public record AuthorDto(Long id, String firstName, String lastName) {

    public static AuthorDto from(Author author) {
        return new AuthorDto(author.getId(), author.getFirstName(), author.getLastName());
    }
}
