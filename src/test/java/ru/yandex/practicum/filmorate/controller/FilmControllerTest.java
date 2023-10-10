package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory valid = Validation.buildDefaultValidatorFactory();


        validator = valid.getValidator();

    }

    @Test
    void addFilm_EmptyName_ReturnsBadRequest() {

        Film film = Film.builder()
                .name("")
                .description("Test description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Название фильма не может быть пустым", violation.getMessage());

    }

    @Test
    void addFilm_LongDescription_ReturnsBadRequest() {

        Film film = Film.builder()
                .name("Test film")
                .description("Random text random text random text random text random text random text random " +
                        "text random text random text random text random text random text random text random text " +
                        "random text random text random text.")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Максимальная длина описания - 200 символов", violation.getMessage());
    }

    @Test
    void addFilm_PreReleaseDate_ReturnsBadRequest() {

        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", violation.getMessage());
    }

    @Test
    void addFilm_NegativeDuration_ReturnsBadRequest() {

        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(0)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Продолжительность фильма должна быть положительной", violation.getMessage());
    }

    /*@Test
    void addFilm_ValidFilm_ReturnsOk() {

        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(120)
                .build();

        try {
            Film addedFilm = filmController.addFilm(film);
            assertEquals(film, addedFilm);
        } catch (ValidationException ex) {
            throw new AssertionError("Unexpected BadRequestException");
        }
    }*/
}

