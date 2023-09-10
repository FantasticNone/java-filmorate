package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {

        log.info("Добавлен новый фильм: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @PathVariable long id, @RequestBody Film film) {

        film.setId(id);
        log.info("Обновлен фильм с id {}.", film.getId());
        return filmService.updateFilm(film);

    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@Valid @PathVariable long id, @RequestBody Film film) throws NotFoundException {

        film.setId(id);
        log.info("Удален фильм с id: {}", id);
        filmService.deleteFilm(film);
    }
}
