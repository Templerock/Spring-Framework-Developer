package ru.otus.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.otus.spring.domain.Author;

import java.util.List;

@RepositoryRestResource(path = "authors")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAllByOrderByLastNameAscFirstNameAsc();
}
