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

        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::rowMapperForGenre);

        return Objects.requireNonNullElseGet(genres, ArrayList::new);
    }

    @Override
    public Genre getGenreById(int genreId) {
        if (!isGenreExist(genreId)) {
            throw new NotFoundException("Жанр с ID " + genreId + " не найден.");
        }
        String sqlQuery = "SELECT genre_id, genre_name FROM genres WHERE genre_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, new Object[]{genreId}, this::rowMapperForGenre);

    }

    private Genre rowMapperForGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private boolean isGenreExist(int genreId) {
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[]{genreId}, Integer.class);
        return count > 0;
    }
}
