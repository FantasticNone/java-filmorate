package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();

    private static long filmId = 1;

    @Override
    public Optional<Film> addFilm(Film film) {

        film.setId(generateFilmId());

        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        Film updatedFilm = films.get(film.getId());

        if (updatedFilm == null) {
            throw new NotFoundException("Фильм по id: " + film.getId() + " не найден.");
        }

        updatedFilm.setName(film.getName());
        updatedFilm.setDescription(film.getDescription());
        updatedFilm.setReleaseDate(film.getReleaseDate());
        updatedFilm.setDuration(film.getDuration());

        return Optional.of(updatedFilm);
    }

    @Override
    public void deleteFilm(long id) {
        Film deletedFilm = films.get(id);

        if (deletedFilm == null) {
            throw new NotFoundException("Фильм с id: " + id + " не найден.");
        }
        films.remove(deletedFilm.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> filmList = new ArrayList<>(films.values());
        return filmList;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    private synchronized long generateFilmId() {
        return filmId++;
    }
}