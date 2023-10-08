package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int id);

    List<Film> getAllFilms();

    Optional<Film> getFilmById(Integer filmId);

    void addLike(int id, int userId);

    void removeLike(int id, int userId);

    List<Film> getTopLikedFilms(int count);

    boolean isFilmExist(Integer id);

}
