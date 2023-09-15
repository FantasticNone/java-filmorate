package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {

        log.info("Добавлен новый фильм: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {

        film.setId(film.getId());
        log.info("Обновлен фильм с id {}.", film.getId());
        return filmService.updateFilm(film);

    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(
            @PathVariable long id,
            @PathVariable long userId
    ) {
        log.info("Добавление лайка к фильму: filmId={}, likeId={}", id, userId);
        filmService.addLike(id, userId);
    }

    @GetMapping
    public List<Film> getAllFilms() {

        log.info("Получение всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {

        log.info("Получение фильма по id: {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @Positive @RequestParam(defaultValue = "10") int count
    ) {
        log.info("Получение топ {} фильмов с наибольшим количеством лайков", count);
        return filmService.getTopLikedFilms(count);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) {

        log.info("Удален фильм с id: {}", id);
        filmService.deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromFilm(
            @PathVariable long id,
            @PathVariable long userId
    ) {
        log.info("Удаление лайка у фильма: filmId={}, likeId={}", id, userId);
        filmService.removeLike(id, userId);
    }
}
