package ru.yandex.practicum.filmorate.service.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT genre_id, genre_name FROM genres";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rowMapperForGenre(rs));

        return Objects.requireNonNullElseGet(genres, ArrayList::new);
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "SELECT genre_id, genre_name FROM genres WHERE genre_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> rowMapperForGenre(rs), id);
        } catch (EmptyResultDataAccessException exc) {
            log.debug("Genre id = {} не найден.", id);
            throw new NotFoundException("Жанр не найден.");
        }
    }

    private Genre rowMapperForGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
