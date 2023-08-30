package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
    public Film addFilm(@Valid @RequestBody Film film) throws BadRequestException {

        try {
            int filmId = Film.filmsId();
            film.setId(filmId);

            films.put(film.getId(), film);
            log.info("Добавлен новый фильм: {}", film);
            return film;

        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            throw new BadRequestException(ex.getErrors());
        }
    }

    @PutMapping("/{id}")
    public Film updateFilm(@Valid @PathVariable int id, @RequestBody Film film) throws BadRequestException, NotFoundException {

        try {
            if (!films.containsKey(id))
                throw new NotFoundException("Фильм по id: " + id + " не найден.");
            Film updatedFilm = films.get(id);

            if (updatedFilm == null)
                throw new NotFoundException("Фильм по id:" + id + " не найден.");

            updatedFilm.setName(film.getName());
            updatedFilm.setDescription(film.getDescription());
            updatedFilm.setReleaseDate(film.getReleaseDate());
            updatedFilm.setDuration(film.getDuration());

            films.replace(film.getId(), updatedFilm);
            log.info("Обновлен фильм с id {}: {}", id, updatedFilm);
            return updatedFilm;

        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            throw new BadRequestException(ex.getErrors());
        } catch (NotFoundException ex) {
            log.error("Ошибка при обновлении фильма: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping
    public List<Film> getAllFilms() {
        List<Film> filmList = new ArrayList<>(films.values());
        return filmList;
    }
}
