package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.dao.impl.MPADbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MPADbStorageTest {
    private final MPADbStorage mpaDbStorage;

    @Test
    void getAllRatings() {
        List<MPA> expectedList = List.of(MPA.builder().id(1).name("G").build(),
                MPA.builder().id(2).name("PG").build(),
                MPA.builder().id(3).name("PG-13").build(),
                MPA.builder().id(4).name("R").build(),
                MPA.builder().id(5).name("NC-17").build()
        );
        List<MPA> actualList;

        actualList = mpaDbStorage.getAllMPA();

        assertArrayEquals(expectedList.toArray(), actualList.toArray());
    }

    @Test
    void getRatingById() {
        MPA expectedRating = MPA.builder().id(3).name("PG-13").build();
        MPA actualRating;

        actualRating = mpaDbStorage.getMPAById(3);

        assertEquals(expectedRating, actualRating);
    }
}