package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {

        log.info("Добавлен новый фильм: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {

        film.setId(film.getId());
        log.info("Обновлен фильм с id {}.", film.getId());
        return filmService.updateFilm(film);

    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLikeToFilm(
            @PathVariable int id,
            @PathVariable int userId
    ) {
        log.info("Добавление лайка к фильму: filmId={}, likeId={}", id, userId);
        filmService.addLike(id, userId);
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {

        log.info("Получение всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {

        log.info("Получение фильма по id: {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(
            @Positive @RequestParam(defaultValue = "10") int count
    ) {
        log.info("Получение топ {} фильмов с наибольшим количеством лайков", count);
        return filmService.getTopLikedFilms(count);
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilm(@PathVariable int id) {

        log.info("Удален фильм с id: {}", id);
        filmService.deleteFilm(id);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLikeFromFilm(
            @PathVariable int id,
            @PathVariable int userId
    ) {
        log.info("Удаление лайка у фильма: filmId={}, likeId={}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return filmService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<MPA> getAllRatings() {
        return filmService.getAllMPA();
    }

    @GetMapping("/mpa/{id}")
    public MPA getRatingById(@PathVariable int id) {
        return filmService.getMPAById(id);
    }
}
