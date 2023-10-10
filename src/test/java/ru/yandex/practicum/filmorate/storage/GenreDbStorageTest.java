package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getAllGenres() {
        List<Genre> expectedList = List.of(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build(),
                Genre.builder().id(3).name("Мультфильм").build(),
                Genre.builder().id(4).name("Триллер").build(),
                Genre.builder().id(5).name("Документальный").build(),
                Genre.builder().id(6).name("Боевик").build());
        List<Genre> actualList;

        actualList = genreDbStorage.getAllGenres();

        assertArrayEquals(expectedList.toArray(), actualList.toArray());
    }

    @Test
    void getGenreById() {
        Genre expectedGenre = Genre.builder().id(3).name("Мультфильм").build();
        Genre actualGenre;

        actualGenre = genreDbStorage.getGenreById(3);

        assertEquals(expectedGenre, actualGenre);
    }
}