package ru.otus.spring.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookRepositoryHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public BookRepositoryHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            Long books = jdbcTemplate.queryForObject("select count(*) from books", Long.class);
            return Health.up()
                    .withDetail("books", books)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getClass().getName())
                    .withDetail("message", e.getMessage())
                    .build();
        }
    }
}
