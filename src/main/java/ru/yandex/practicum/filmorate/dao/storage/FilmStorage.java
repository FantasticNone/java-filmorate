package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int id);

    List<Film> getAllFilms();

    Film getFilmById(Integer filmId);

    void addLike(int id, int userId);

    void removeLike(int id, int userId);

    List<Film> getTopLikedFilms(int count);

    boolean isFilmExist(Integer id);

}
