package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dao.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    protected LinkedHashSet<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT g.genre_id, g.genre_name FROM genres g " +
                "JOIN film_genres fg ON g.genre_id=fg.genre_id " +
                "WHERE fg.film_id = ?";

        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, this::rowMapperForGenre, filmId));
    }

    protected void deleteGenresByFilmId(int filmId) {
        String sqlDeleteQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteQuery, filmId);
    }

    protected void updateGenres(Film film) {
        int filmId = film.getId();
        LinkedHashSet<Genre> genres = film.getGenres();
        String sqlDeleteQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteQuery, filmId);

        String sqlInsertQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        List<Genre> genreList = new ArrayList<>(genres);

        jdbcTemplate.batchUpdate(
                sqlInsertQuery,
                genreList,
                genreList.size(),
                (ps, genre) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genre.getId());
                }
        );
        log.debug("Обновлены жанры.");
    }
}
