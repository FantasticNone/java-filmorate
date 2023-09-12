package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

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
        if (filmId < 0 || likeId < 0) {
            throw new NotFoundException("Значения не могут быть отрицательными.");
        }
        Film film = getFilmById(filmId);
        film.removeLike(likeId);
    }

    public List<Film> getTopLikedFilms(int count) {
        return filmStorage.getAllFilms()
                .stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getAllFilms()
                .stream()
                .filter(film -> film.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм по id: " + filmId + " не найден."));
    }
}