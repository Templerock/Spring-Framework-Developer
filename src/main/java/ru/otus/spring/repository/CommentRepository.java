package ru.otus.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.otus.spring.domain.Comment;

import java.util.List;

@RepositoryRestResource(path = "comments")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.book order by c.id")
    List<Comment> findAllWithBook();

    List<Comment> findAllByBookId(Long bookId);
}
