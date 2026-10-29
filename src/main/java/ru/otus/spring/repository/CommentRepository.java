package ru.otus.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.spring.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.book order by c.id")
    List<Comment> findAllWithBook();

    List<Comment> findAllByBookId(Long bookId);
}
