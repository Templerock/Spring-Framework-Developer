package ru.otus.spring.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDao {

    private static final RowMapper<Genre> GENRE_ROW_MAPPER =
            (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name"));

    private final NamedParameterJdbcTemplate jdbc;

    public GenreDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Genre> getById(long id) {
        List<Genre> genres = jdbc.query("""
                        select id, name
                        from genres
                        where id = :id
                        """,
                new MapSqlParameterSource("id", id),
                GENRE_ROW_MAPPER);
        return genres.stream().findFirst();
    }

    public List<Genre> getAll() {
        return jdbc.query("""
                        select id, name
                        from genres
                        order by name
                        """,
                GENRE_ROW_MAPPER);
    }

    public long insert(Genre genre) {
        var params = new MapSqlParameterSource()
                .addValue("name", genre.getName());
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update("""
                        insert into genres (name)
                        values (:name)
                        """,
                params,
                keyHolder);
        long id = keyHolder.getKey().longValue();
        genre.setId(id);
        return id;
    }

    public void update(Genre genre) {
        jdbc.update("""
                        update genres
                        set name = :name
                        where id = :id
                        """,
                new MapSqlParameterSource()
                        .addValue("id", genre.getId())
                        .addValue("name", genre.getName()));
    }

    public void deleteById(long id) {
        jdbc.update("""
                        delete from genres
                        where id = :id
                        """,
                new MapSqlParameterSource("id", id));
    }

    public long count() {
        return jdbc.queryForObject("select count(*) from genres", new MapSqlParameterSource(), Long.class);
    }
}
