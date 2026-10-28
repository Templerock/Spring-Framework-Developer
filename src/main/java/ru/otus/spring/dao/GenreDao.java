package ru.otus.spring.dao;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDao {

    private final EntityManager em;

    public GenreDao(EntityManager em) {
        this.em = em;
    }

    public Optional<Genre> getById(long id) {
        return Optional.ofNullable(em.find(Genre.class, id));
    }

    public List<Genre> getAll() {
        return em.createQuery("select g from Genre g order by g.name", Genre.class)
                .getResultList();
    }

    public Genre save(Genre genre) {
        if (genre.getId() == 0) {
            em.persist(genre);
            return genre;
        }
        return em.merge(genre);
    }

    public void delete(Genre genre) {
        em.remove(genre);
    }

    public long count() {
        return em.createQuery("select count(g) from Genre g", Long.class).getSingleResult();
    }
}
