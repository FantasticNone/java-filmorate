package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    MPA rating;
    List<Genre> genres;
    Film testFilm;
    Film testFilmTwo;
    User testUser;
    User testUserTwo;

    @BeforeEach
    public void initObjects() {
        rating = MPA.builder().id(5).name("NC-17").build();
        genres = List.of(Genre.builder().id(5).name("Документальный").build(),
                Genre.builder().id(6).name("Боевик").build());
        testFilm = Film.builder().name("test").mpa(rating).description("test description")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(100).genres(genres)
                .build();
        testFilmTwo = Film.builder().name("test2").mpa(rating).description("test description two")
                .releaseDate(LocalDate.of(1950, 12, 12)).duration(200).genres(genres)
                .build();
        testUser = User.builder().name("tom").email("email@test.ru").login("anderson")
                .birthday(LocalDate.of(2000, 12, 12)).build();
        testUserTwo = User.builder().name("john").email("email2@test.ru").login("bjornson")
                .birthday(LocalDate.of(2000, 12, 12)).build();
    }

    @Test
    void createFilm() {
        Film expectedFilm;
        Film actualFilm;

        actualFilm = filmDbStorage.addFilm(testFilm);
        testFilm.setId(1);
        expectedFilm = testFilm;

        assertEquals(expectedFilm, actualFilm);
    }

    /*@Test
    void getFilmById() {
        Film expectedFilm = Film.builder()
                .id(2)
                .name("test")
                .mpa(rating)
                .description("test description")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(100)
                .genres(genres)
                .build();

        filmDbStorage.addFilm(testFilm);
        filmDbStorage.addFilm(testFilm);
        Optional<Film> actualFilm = filmDbStorage.getFilmById(2);

        assertEquals(expectedFilm, actualFilm);
    }*/

   @Test
    void updateFilm() {
        Film expectedFilm;
        Film actualFilm;

        filmDbStorage.addFilm(testFilm);
        testFilmTwo.setId(1);
        expectedFilm = filmDbStorage.updateFilm(testFilmTwo);
        actualFilm = testFilmTwo;

        assertEquals(expectedFilm, actualFilm);
    }

    @Test
    void removeFilm(){
        filmDbStorage.addFilm(testFilm);
        testFilm.setId(1);
        filmDbStorage.addFilm(testFilmTwo);
        testFilmTwo.setId(2);
        assertEquals(testFilm,filmDbStorage.getFilmById(1) );
        assertEquals(2, filmDbStorage.getAllFilms().size());

        filmDbStorage.deleteFilm(1);

        assertThrows(NotFoundException.class, () -> filmDbStorage.getFilmById(1));
        assertEquals(1,filmDbStorage.getAllFilms().size());
    }

    @Test
    void getAllFilms() {
        List<Film> expectedFilms;
        List<Film> actualFilms;

        testFilm.setId(1);
        testFilmTwo.setId(2);
        expectedFilms = List.of(testFilm,
                testFilmTwo);
        filmDbStorage.addFilm(testFilm);
        filmDbStorage.addFilm(testFilmTwo);
        actualFilms = filmDbStorage.getAllFilms();

        assertArrayEquals(expectedFilms.toArray(), actualFilms.toArray());
    }

    @Test
    void getTopLikedFilms() {
        userDbStorage.createUser(testUser);
        userDbStorage.createUser(testUserTwo);

        Film film1 = filmDbStorage.addFilm(testFilm);
        Film film2 = filmDbStorage.addFilm(testFilmTwo);

        filmDbStorage.addLike(film1.getId(), testUser.getId());
        filmDbStorage.addLike(film1.getId(), testUserTwo.getId());
        filmDbStorage.addLike(film2.getId(), testUser.getId());

        List<Film> topLikedFilms = filmDbStorage.getTopLikedFilms(2);

        assertEquals(2, topLikedFilms.size());
        assertEquals(film1, topLikedFilms.get(0));
        assertEquals(film2, topLikedFilms.get(1));
    }

    @Test
    void isFilmExist_ValidFilmId() {
        Film film = filmDbStorage.addFilm(testFilm);
        assertTrue(filmDbStorage.isFilmExist(film.getId()));
    }
}
