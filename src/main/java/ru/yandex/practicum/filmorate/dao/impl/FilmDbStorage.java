package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.dao.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final GenreDbStorage genreDbStorage;

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
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

        if (film.getGenres() != null) {
            genreDbStorage.updateGenres(film);
        } else {
            log.debug("Жанры отсутствуют.");
        }

        return film;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        String sqlQuery = "select " +
                "f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "m.mpa_name, " +
                "count(l.user_id) as likes " +
                "from films AS f left join mpa as m on f.mpa_id = m.mpa_id " +
                "left join likes As l on f.film_id = l.film_id " +
                "where f.film_id = ? " +
                "group by f.film_id ";

        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, new Object[]{filmId}, this::rowMapperForFilm);

            if (film.getGenres() != null && film.getGenres().size() > 0) {
                genreDbStorage.updateGenres(film);
            }

            return film;

        } catch (EmptyResultDataAccessException exc) {
            log.debug("Фильм по id - {} не найден.", filmId);
            throw new NotFoundException("Фильм не найден.");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (!isFilmExist(film.getId())) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден.");
        }

        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ? WHERE film_id = ?";

        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (film.getGenres() != null && film.getGenres().size() > 0) {
            genreDbStorage.updateGenres(film);
        } else {
            genreDbStorage.deleteGenresByFilmId(film.getId());
        }

        return getFilmById(film.getId());
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
        String sqlQuery = "SELECT f.film_id, f.name, m.mpa_id, m.mpa_name, " +
                "f.description, f.release_date, f.duration, " +
                "COUNT(fl.user_id) likes " +
                "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes fl ON f.film_id=fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC";

        final Map<Integer, Film> films = new HashMap<>();

        jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            int filmId = rs.getInt("film_id");
            Film film = films.get(filmId);

            if (film == null) {
                film = Film.builder()
                        .id(rs.getInt("film_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .mpa(rowMapperForRating(rs))
                        .genres(new LinkedHashSet<>())
                        .likes(rs.getInt("likes"))
                        .build();

                films.put(filmId, film);
            }

            return film;
        });

        genreDbStorage.addGenresToFilms(films);

        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) " +
                "VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        } catch (DataAccessException exc) {
            log.debug("Дублирование лайка от user id - {} к фильму film id {}", userId, filmId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

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
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "m.mpa_id, m.mpa_name, COUNT(l.user_id) AS likes " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name " +
                "ORDER BY likes DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sqlQuery, this::rowMapperForFilm, count);
    }

    @Override
    public boolean isFilmExist(Integer filmId) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";

        Boolean exists = jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId);

        return exists != null && exists;
    }

    private Film rowMapperForFilm(ResultSet rs, int rowNum) throws SQLException {

        int filmId = rs.getInt("film_id");
        LinkedHashSet<Genre> genres = genreDbStorage.getGenresByFilmId(filmId);

        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(rowMapperForRating(rs))
                .genres(genres)
                .likes(rs.getInt("likes"))
                .build();
    }

    private MPA rowMapperForRating(ResultSet rs) throws SQLException {
        return MPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
