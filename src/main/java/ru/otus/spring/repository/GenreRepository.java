package ru.otus.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.otus.spring.domain.Genre;

import java.util.List;

@RepositoryRestResource(path = "genres")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findAllByOrderByNameAsc();
}
