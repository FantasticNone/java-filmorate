package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public ResponseEntity<?> addFilm(@RequestBody Film film) {
        List<String> errors = new ArrayList<>();

        try {
            int filmId = Film.filmsId();
            film.setId(filmId);

            if (film.getName().isEmpty())
                errors.add("Название фильма не может быть пустым");
            if (film.getDescription().length() > 200)
                errors.add("Максимальная длина описания - 200 символов");
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
                errors.add("Дата релиза не может быть раньше 28 декабря 1895 года");
            if (film.getDuration() <= 0)
                errors.add("Продолжительность фильма должна быть положительной");

            if (!errors.isEmpty())
                throw new ValidationException(errors);

            films.put(film.getId(), film);
            log.info("Добавлен новый фильм: {}", film);
            return ResponseEntity.ok(film);

        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrors());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFilm(@PathVariable int id, @RequestBody Film film) {
        List<String> errors = new ArrayList<>();

        try {
            Film updatedFilm = films.get(id);

            if (updatedFilm == null)
                throw new IllegalArgumentException("Фильм по id:" + id + " не найден.");

            if (film.getName().isEmpty())
                errors.add("Название фильма не может быть пустым");
            if (film.getDescription().length() > 200)
                errors.add("Максимальная длина описания - 200 символов");
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
                errors.add("Дата релиза не может быть раньше 28 декабря 1895 года");
            if (film.getDuration() <= 0)
                errors.add("Продолжительность фильма должна быть положительной");

            if (!errors.isEmpty())
                throw new ValidationException(errors);

            updatedFilm.setName(film.getName());
            updatedFilm.setDescription(film.getDescription());
            updatedFilm.setReleaseDate(film.getReleaseDate());
            updatedFilm.setDuration(film.getDuration());

            films.replace(film.getId(), updatedFilm);
            log.info("Обновлен фильм с id {}: {}", id, updatedFilm);
            return ResponseEntity.ok(updatedFilm);

        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrors());
        } catch (IllegalArgumentException ex) {
            log.error("Ошибка при обновлении фильма: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм не найден");
        }
    }

    @PutMapping
    public ResponseEntity<?> unknownFilm(@RequestBody Film film) {
        List<String> errors = new ArrayList<>();

        try {
            if (film.getId() == null)
                errors.add("id не может быть пустым");
            if (film.getName().isEmpty())
                errors.add("Название фильма не может быть пустым");
            if (film.getDescription().length() > 200)
                errors.add("Максимальная длина описания - 200 символов");
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
                errors.add("Дата релиза не может быть раньше 28 декабря 1895 года");
            if (film.getDuration() <= 0)
                errors.add("Продолжительность фильма должна быть положительной");

            if (!errors.isEmpty())
                throw new ValidationException(errors);

            films.put(film.getId(), film);
            log.info("Обновлен фильм с id {}: {}", film.getId(), film);
            return ResponseEntity.ok(film);

        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrors());
        } catch (IllegalArgumentException ex) {
            log.error("Ошибка при обновлении фильма: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм не найден");
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> filmList = new ArrayList<>(films.values());
        return ResponseEntity.ok(filmList);
    }
}

