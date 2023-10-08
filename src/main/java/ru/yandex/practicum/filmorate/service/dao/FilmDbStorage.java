package ru.yandex.practicum.filmorate.service.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("filmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Film addFilm(Film film) {
        String sqlQuery =
                "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                        "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Integer filmId) {
        String sqlQuery =
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.likes, " +
                        "m.mpa_id, m.mpa_name " +
                        "FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "WHERE f.film_id = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rowMapperForFilm(rs), filmId);

        return films.isEmpty() ? Optional.empty() : films.stream().findFirst();
    }

    @Override
    public Film updateFilm(Film film) {
        if (!isFilmExist(film.getId())) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден.");
        }

        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                " duration = ?, mpa_id = ? WHERE film_id = ?";

        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        return film;
    }

    @Override
    public void deleteFilm(int id) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";

        int rowsAffected = jdbcTemplate.update(sqlQuery, id);

        if (rowsAffected == 0) {
            log.debug("Фильм с ID {} не найден.", id);
            throw new NotFoundException("Фильм с ID " + id + " не найден.");
        } else {
            log.info("Фильм с ID {} удален.", id);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String getAllFilmsQuery =
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name " +
                        "FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id";

        return jdbcTemplate.query(getAllFilmsQuery, (rs, rowNum) -> rowMapperForFilm(rs));
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) " +
                "VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        } catch (DataAccessException exc) {
            log.debug("Дублирование лайка от user id - {} к фильму film id {}", userId, filmId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

        int rowsAffected = jdbcTemplate.update(sqlQuery, filmId, userId);

        if (rowsAffected == 0) {
            log.debug("Лайк от пользователя с ID {} к фильму с ID {} не найден.", userId, filmId);
            throw new NotFoundException("Лайк не найден.");
        } else {
            log.info("Лайк от пользователя с ID {} к фильму с ID {} удален.", userId, filmId);
        }
    }

    @Override
    public List<Film> getTopLikedFilms(int count) {
        String sqlQuery =
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.likes, " +
                        "m.mpa_id, m.mpa_name " +
                        "FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "ORDER BY f.likes DESC " +
                        "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rowMapperForFilm(rs), count);
    }

    @Override
    public boolean isFilmExist(Integer filmId) {
        String sqlQuery = "SELECT COUNT(*) FROM films WHERE film_id = ?";

        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId);

        return count != null && count > 0;
    }

    public int getLikesCount(int filmId) {
        String sqlQuery = "SELECT COUNT(*) FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId);
    }

    private void updateGenres(Film film) {
        int filmId = film.getId();
        List<Genre> genres = film.getGenres().stream()
                .distinct().collect(Collectors.toList());
        String sqlDeleteQuery = "DELETE FROM film_genres WHERE film_id = ?";
        String sqlInsertQuery = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlDeleteQuery, filmId);

        jdbcTemplate.batchUpdate(
                sqlInsertQuery,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    private Film rowMapperForFilm(ResultSet rs) throws SQLException {
        MPA rating = MPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();

        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(rating)
                .likes(rs.getInt("likes"))
                .build();
    }

    public List<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT g.genre_id, g.genre_name FROM genres g " +
                "JOIN film_genres fg ON g.genre_id=fg.genre_id " +
                "WHERE fg.film_id = ?";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rowMapperForGenre(rs));

        return Objects.requireNonNullElseGet(genres, ArrayList::new);
    }

    private Genre rowMapperForGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private MPA rowMapperForRating(ResultSet rs) throws SQLException {
        return MPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}