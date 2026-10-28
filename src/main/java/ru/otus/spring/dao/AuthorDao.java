package ru.otus.spring.dao;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Author;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthorDao {

    private final EntityManager em;

    public AuthorDao(EntityManager em) {
        this.em = em;
    }

    public Optional<Author> getById(long id) {
        return Optional.ofNullable(em.find(Author.class, id));
    }

    public List<Author> getAll() {
        return em.createQuery("select a from Author a order by a.lastName, a.firstName", Author.class)
                .getResultList();
    }

    public Author save(Author author) {
        if (author.getId() == 0) {
            em.persist(author);
            return author;
        }
        return em.merge(author);
    }

    public void delete(Author author) {
        em.remove(author);
    }

    public long count() {
        return em.createQuery("select count(a) from Author a", Long.class).getSingleResult();
    }
}
