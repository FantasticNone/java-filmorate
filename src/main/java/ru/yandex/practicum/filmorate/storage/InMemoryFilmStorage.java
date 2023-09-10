package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        int filmId = Film.filmsId();
        film.setId(filmId);

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film updatedFilm = films.get(film.getId());

        if (updatedFilm == null) {
            throw new NotFoundException("Фильм по id: " + film.getId() + " не найден.");
        }

        updatedFilm.setName(film.getName());
        updatedFilm.setDescription(film.getDescription());
        updatedFilm.setReleaseDate(film.getReleaseDate());
        updatedFilm.setDuration(film.getDuration());

        return updatedFilm;
    }

    @Override
    public void deleteFilm(Film film) {
        Film deletedFilm = films.get(film.getId());

        if (deletedFilm == null) {
            throw new NotFoundException("Фильм с id: " + film.getId() + " не найден.");
        }
        films.remove(deletedFilm.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> filmList = new ArrayList<>(films.values());
        return filmList;
    }
}