package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private List<Film> films = new ArrayList<>();

    @PostMapping
    public ResponseEntity<?> addFilm(@RequestBody Film film) {
        List<String> errors = new ArrayList<>();

        try {
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

            films.add(film);
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
            Film updatedFilm = films.stream()
                    .filter(f -> f.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Фильм по id:" + id + " не найден."));

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

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }

    public void setFilms(List<Film> films) {
        this.films = films;
    }
}

