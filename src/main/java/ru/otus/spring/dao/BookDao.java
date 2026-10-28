package ru.otus.spring.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class BookDao {

    private static final String BOOK_COLUMNS = """
            b.id,
            b.title,
            a.id      as author_id,
            a.first_name as author_first_name,
            a.last_name  as author_last_name,
            g.id      as genre_id,
            g.name    as genre_name
            """;

    private static final RowMapper<Book> BOOK_ROW_MAPPER = (rs, rowNum) -> new Book(
            rs.getLong("id"),
            rs.getString("title"),
            new Author(rs.getLong("author_id"), rs.getString("author_first_name"), rs.getString("author_last_name")),
            new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));

    private final NamedParameterJdbcTemplate jdbc;

    public BookDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Book> getById(long id) {
        List<Book> books = jdbc.query("""
                        select %s
                        from books b
                        join authors a on a.id = b.author_id
                        join genres g on g.id = b.genre_id
                        where b.id = :id
                        """.formatted(BOOK_COLUMNS),
                new MapSqlParameterSource("id", id),
                BOOK_ROW_MAPPER);
        return books.stream().findFirst();
    }

    public List<Book> getAll() {
        return jdbc.query("""
                        select %s
                        from books b
                        join authors a on a.id = b.author_id
                        join genres g on g.id = b.genre_id
                        order by b.title
                        """.formatted(BOOK_COLUMNS),
                BOOK_ROW_MAPPER);
    }

    public long insert(Book book) {
        var params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId())
                .addValue("genre_id", book.getGenre().getId());
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update("""
                        insert into books (title, author_id, genre_id)
                        values (:title, :author_id, :genre_id)
                        """,
                params,
                keyHolder);
        long id = keyHolder.getKey().longValue();
        book.setId(id);
        return id;
    }

    public void update(Book book) {
        jdbc.update("""
                        update books
                        set title = :title, author_id = :author_id, genre_id = :genre_id
                        where id = :id
                        """,
                new MapSqlParameterSource()
                        .addValue("id", book.getId())
                        .addValue("title", book.getTitle())
                        .addValue("author_id", book.getAuthor().getId())
                        .addValue("genre_id", book.getGenre().getId()));
    }

    public void deleteById(long id) {
        jdbc.update("""
                        delete from books
                        where id = :id
                        """,
                new MapSqlParameterSource("id", id));
    }

    public long count() {
        return jdbc.queryForObject("select count(*) from books", new MapSqlParameterSource(), Long.class);
    }
}
