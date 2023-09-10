package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(Long filmId, Long likeId) {
        Film film = getFilmById(filmId);
        film.addLike(likeId);
    }

    public void removeLike(Long filmId, Long likeId) {
        Film film = getFilmById(filmId);
        film.removeLike(likeId);
    }

    public List<Film> getTopLikedFilms(int limit) {
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Film getFilmById(Long filmId) {
        return filmStorage.getAllFilms()
                .stream()
                .filter(film -> film.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм по id: " + filmId + " не найден."));
    }
}