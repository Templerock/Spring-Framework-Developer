package ru.otus.spring.dao;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Book;

import java.util.List;
import java.util.Optional;

@Repository
public class BookDao {

    private final EntityManager em;

    public BookDao(EntityManager em) {
        this.em = em;
    }

    public Optional<Book> getById(long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    public List<Book> getAll() {
        return em.createQuery("select b from Book b join fetch b.author join fetch b.genre order by b.title", Book.class)
                .getResultList();
    }

    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }

    public void delete(Book book) {
        em.remove(book);
    }

    public long count() {
        return em.createQuery("select count(b) from Book b", Long.class).getSingleResult();
    }
}
