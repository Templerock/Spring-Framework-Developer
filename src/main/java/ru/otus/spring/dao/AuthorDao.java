package ru.otus.spring.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Author;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthorDao {

    private static final RowMapper<Author> AUTHOR_ROW_MAPPER =
            (rs, rowNum) -> new Author(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"));

    private final NamedParameterJdbcTemplate jdbc;

    public AuthorDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Author> getById(long id) {
        List<Author> authors = jdbc.query("""
                        select id, first_name, last_name
                        from authors
                        where id = :id
                        """,
                new MapSqlParameterSource("id", id),
                AUTHOR_ROW_MAPPER);
        return authors.stream().findFirst();
    }

    public List<Author> getAll() {
        return jdbc.query("""
                        select id, first_name, last_name
                        from authors
                        order by last_name, first_name
                        """,
                AUTHOR_ROW_MAPPER);
    }

    public long insert(Author author) {
        var params = new MapSqlParameterSource()
                .addValue("first_name", author.getFirstName())
                .addValue("last_name", author.getLastName());
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update("""
                        insert into authors (first_name, last_name)
                        values (:first_name, :last_name)
                        """,
                params,
                keyHolder);
        long id = keyHolder.getKey().longValue();
        author.setId(id);
        return id;
    }

    public void update(Author author) {
        jdbc.update("""
                        update authors
                        set first_name = :first_name, last_name = :last_name
                        where id = :id
                        """,
                new MapSqlParameterSource()
                        .addValue("id", author.getId())
                        .addValue("first_name", author.getFirstName())
                        .addValue("last_name", author.getLastName()));
    }

    public void deleteById(long id) {
        jdbc.update("""
                        delete from authors
                        where id = :id
                        """,
                new MapSqlParameterSource("id", id));
    }

    public long count() {
        return jdbc.queryForObject("select count(*) from authors", new MapSqlParameterSource(), Long.class);
    }
}
