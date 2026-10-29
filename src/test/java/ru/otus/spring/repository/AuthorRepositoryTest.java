package ru.otus.spring.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.spring.domain.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void savedAuthorCanBeReadById() {
        var author = authorRepository.save(new Author(0, "Lev", "Tolstoy"));

        assertThat(author.getId()).isPositive();
        assertThat(authorRepository.findById(author.getId()))
                .get()
                .extracting(Author::getFirstName, Author::getLastName)
                .containsExactly("Lev", "Tolstoy");
    }

    @Test
    void findAllByOrderByLastNameAscFirstNameAscReturnsSorted() {
        authorRepository.save(new Author(0, "Vladimir", "Nabokov"));
        authorRepository.save(new Author(0, "Alex", "Pushkin"));

        var all = authorRepository.findAllByOrderByLastNameAscFirstNameAsc();

        assertThat(all).isNotEmpty()
                .extracting(Author::getLastName)
                .contains("Nabokov", "Pushkin")
                .isSortedAccordingTo(String::compareTo);
    }

    @Test
    void findAllByOrderByLastNameAscFirstNameAscIncludesSeededAuthors() {
        assertThat(authorRepository.findAllByOrderByLastNameAscFirstNameAsc()).isNotEmpty();
    }

    @Test
    void getByIdReturnsEmptyForUnknownId() {
        assertThat(authorRepository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateChangesFirstAndLastName() {
        var author = authorRepository.save(new Author(0, "Old", "Name"));

        authorRepository.save(new Author(author.getId(), "New", "Name"));

        assertThat(authorRepository.findById(author.getId()))
                .get()
                .extracting(Author::getFirstName, Author::getLastName)
                .containsExactly("New", "Name");
    }

    @Test
    void deleteRemovesAuthor() {
        var author = authorRepository.save(new Author(0, "To", "Delete"));

        authorRepository.delete(author);

        assertThat(authorRepository.findById(author.getId())).isEmpty();
    }

    @Test
    void countMatchesInsertions() {
        long before = authorRepository.count();

        authorRepository.save(new Author(0, "One", "Author"));

        assertThat(authorRepository.count()).isEqualTo(before + 1);
    }
}
