package ru.otus.spring.dao;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentDao {

    private final EntityManager em;

    public CommentDao(EntityManager em) {
        this.em = em;
    }

    public Optional<Comment> getById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    public List<Comment> getAll() {
        return em.createQuery("select c from Comment c join fetch c.book order by c.id", Comment.class)
                .getResultList();
    }

    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }

    public long count() {
        return em.createQuery("select count(c) from Comment c", Long.class).getSingleResult();
    }
}
