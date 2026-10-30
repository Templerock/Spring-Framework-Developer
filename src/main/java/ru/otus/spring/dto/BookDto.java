package ru.otus.spring.dto;

import ru.otus.spring.domain.Book;

public record BookDto(Long id, String title, Long authorId, Long genreId) {

    public static BookDto from(Book book) {
        return new BookDto(book.getId(), book.getTitle(), book.getAuthor().getId(), book.getGenre().getId());
    }
}
