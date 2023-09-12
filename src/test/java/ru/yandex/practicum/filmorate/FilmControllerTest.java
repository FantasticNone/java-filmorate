package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {
    private FilmController filmController;

    private Validator validator;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        filmController.getAllFilms();


        validator = Validation.buildDefaultValidatorFactory().getValidator();

    }

    @Test
    void addFilm_EmptyName_ReturnsBadRequest() {

        Film film = new Film();
        film.setName("");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2021, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Название фильма не может быть пустым", violation.getMessage());

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

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Максимальная длина описания - 200 символов", violation.getMessage());
    }

    @Test
    void addFilm_PreReleaseDate_ReturnsBadRequest() {

        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", violation.getMessage());
    }

    @Test
    void addFilm_NegativeDuration_ReturnsBadRequest() {

        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Продолжительность фильма должна быть положительной", violation.getMessage());
    }

    @Test
    void addFilm_ValidFilm_ReturnsOk() {

        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2021, 1, 1));
        film.setDuration(120);

        try {
            Film addedFilm = filmController.addFilm(film);
            assertEquals(film, addedFilm);
        } catch (ValidationException ex) {
            throw new AssertionError("Unexpected BadRequestException");
        }
    }
}

