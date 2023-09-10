package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;

    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {

        log.info("Добавлен новый фильм: {}", film);
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @PathVariable int id, @RequestBody Film film) {

        film.setId(id);
        log.info("Обновлен фильм с id {}.", film.getId());
        return filmStorage.updateFilm(film);

    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }
    @DeleteMapping("/{id}")
    public void deleteFilm(@Valid @PathVariable int id, @RequestBody Film film) throws NotFoundException {

        film.setId(id);
        log.info("Удален фильм с id: {}", id);
        filmStorage.deleteFilm(film);
    }
}
