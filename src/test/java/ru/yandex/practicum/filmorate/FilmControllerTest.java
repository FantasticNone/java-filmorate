package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTest {
    private FilmController filmController;
    private List<Film> films;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        films = new ArrayList<>();
        filmController.setFilms(films);
    }

    @Test
    void addFilm_EmptyName_ReturnsBadRequest() {

        Film film = new Film();
        film.setName("");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2021, 1, 1));
        film.setDuration(120);

        ResponseEntity<?> responseEntity = filmController.addFilm(film);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List);
        List<String> errors = (List<String>) responseEntity.getBody();
        assertEquals(1, errors.size());
        assertEquals("Название фильма не может быть пустым", errors.get(0));
    }

    @Test
    void addFilm_LongDescription_ReturnsBadRequest() {

        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Random text random text random text random text random text random text random " +
                "text random text random text random text random text random text random text random text " +
                "random text random text random text.");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120);

        ResponseEntity<?> responseEntity = filmController.addFilm(film);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List);
        List<String> errors = (List<String>) responseEntity.getBody();
        assertEquals(1, errors.size());
        assertEquals("Максимальная длина описания - 200 символов", errors.get(0));
    }

    @Test
    void addFilm_PreReleaseDate_ReturnsBadRequest() {

        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        ResponseEntity<?> responseEntity = filmController.addFilm(film);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List);
        List<String> errors = (List<String>) responseEntity.getBody();
        assertEquals(1, errors.size());
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", errors.get(0));
    }

    @Test
    void addFilm_NegativeDuration_ReturnsBadRequest() {

        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(0);

        ResponseEntity<?> responseEntity = filmController.addFilm(film);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List);
        List<String> errors = (List<String>) responseEntity.getBody();
        assertEquals(1, errors.size());
        assertEquals("Продолжительность фильма должна быть положительной", errors.get(0));
    }

    @Test
    void addFilm_ValidFilm_ReturnsOk() {

        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2021, 1, 1));
        film.setDuration(120);

        ResponseEntity<?> responseEntity = filmController.addFilm(film);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}


