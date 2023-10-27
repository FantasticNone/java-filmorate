package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.dao.storage.FilmStorage;
import ru.yandex.practicum.filmorate.dao.storage.GenreStorage;
import ru.yandex.practicum.filmorate.dao.storage.MPAStorage;
import ru.yandex.practicum.filmorate.dao.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MPAStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       MPAStorage mpaStorage,
                       GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addLike(Integer filmId, Integer userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        filmStorage.addLike(filmId, userId);
        return getFilmById(filmId);
    }

    public Film removeLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            throw new NotFoundException("Значения не могут быть отрицательными.");
        }
        checkFilmId(filmId);
        checkUserId(userId);
        filmStorage.removeLike(filmId, userId);
        return getFilmById(filmId);
    }

    public List<Film> getTopLikedFilms(int count) {
        return filmStorage.getTopLikedFilms(count);
    }

    private void checkFilmId(Integer filmId) {
        if (!filmStorage.isFilmExist(filmId)) {
            throw new NotFoundException("Фильм " + filmId + " не найден.");
        }
    }

    private void checkUserId(Integer userId) {
        if (!userStorage.isUserExist(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден.");
        }
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<MPA> getAllMPA() {
        return mpaStorage.getAllMPA();
    }

    public MPA getMPAById(int id) {
        return mpaStorage.getMPAById(id);
    }
}