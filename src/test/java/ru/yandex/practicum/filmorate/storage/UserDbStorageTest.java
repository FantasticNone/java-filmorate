package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    User testUserOne;
    User testUserTwo;
    User testUserThree;

    @BeforeEach
    public void initUsers() {
        testUserOne = User.builder().name("tom").email("email@test.ru").login("anderson")
                .birthday(LocalDate.of(2000, 12, 12)).build();
        testUserTwo = User.builder().name("john").email("email2@test.ru").login("bjornson")
                .birthday(LocalDate.of(2000, 12, 12)).build();
        testUserThree = User.builder().name("John").email("testJohn@email.com")
                .login("JohnMohn").birthday(LocalDate.of(2010, 12, 12)).build();
    }

    @Test
    void createUser() {
        User expectedUser = testUserOne;
        User actualUser = userDbStorage.createUser(testUserOne);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUser() {
        User expectedUser = testUserThree;
        User actualUser;

        userDbStorage.createUser(testUserOne);
        userDbStorage.createUser(testUserTwo);
        userDbStorage.createUser(testUserThree);
        actualUser = userDbStorage.getUserById(testUserThree.getId()).orElse(null);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void updateUser() {
        User expectedUser;
        User actualUser;

        userDbStorage.createUser(testUserOne);
        testUserTwo.setId(testUserOne.getId());
        expectedUser = testUserTwo;
        userDbStorage.updateUser(testUserTwo);
        actualUser = userDbStorage.getUserById(testUserOne.getId()).orElse(null);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getAllUsers() {
        List<User> expectedList = List.of(testUserOne, testUserTwo, testUserThree);
        List<User> actualList;

        userDbStorage.createUser(testUserOne);
        userDbStorage.createUser(testUserTwo);
        userDbStorage.createUser(testUserThree);
        actualList = userDbStorage.getAllUsers();

        assertArrayEquals(expectedList.toArray(), actualList.toArray());
    }

    @Test
    void addFriend() {
        User expecteUser = testUserThree;
        User actualUser;

        userDbStorage.createUser(testUserOne);
        userDbStorage.createUser(testUserTwo);
        userDbStorage.createUser(testUserThree);
        userDbStorage.addFriend(1, 3);
        actualUser = userDbStorage.getFriends(1).get(0);

        assertEquals(expecteUser, actualUser);
    }

    @Test
    void removeFriend() {
        List<User> expecteFriendList = List.of(testUserTwo);
        List<User> actualFriendList;

        userDbStorage.createUser(testUserOne);
        userDbStorage.createUser(testUserTwo);
        userDbStorage.createUser(testUserThree);
        userDbStorage.addFriend(1, 2);
        userDbStorage.addFriend(1, 3);
        userDbStorage.removeFriend(1, 3);
        actualFriendList = userDbStorage.getFriends(1);

        assertTrue(expecteFriendList.size() == actualFriendList.size());
    }

    @Test
    void getFriends() {
        List<User> expecteFriendList = List.of(testUserTwo, testUserThree);
        List<User> actualFriendList;

        userDbStorage.createUser(testUserOne);
        userDbStorage.createUser(testUserTwo);
        userDbStorage.createUser(testUserThree);
        userDbStorage.addFriend(1, 2);
        userDbStorage.addFriend(1, 3);
        actualFriendList = userDbStorage.getFriends(1);

        assertArrayEquals(expecteFriendList.toArray(), actualFriendList.toArray());
    }

   /* @Test
    void getCommonFriends() {
        List<User> expectedFriendList = List.of(testUserTwo);
        List<User> actualFriendList;

        userDbStorage.createUser(testUserOne);
        userDbStorage.createUser(testUserTwo);
        userDbStorage.createUser(testUserThree);
        userDbStorage.addFriend(testUserOne.getId(), testUserTwo.getId());
        userDbStorage.addFriend(testUserThree.getId(), testUserTwo.getId());
        actualFriendList = userDbStorage.getCommonFriends(testUserOne.getId(), testUserThree.getId());

        assertEquals(expectedFriendList.size(), actualFriendList.size());
        assertTrue(actualFriendList.stream().allMatch(Optional::isPresent));
        List<User> actualFriendListContent = actualFriendList.stream()
                .map(Optional::get)
                .collect(Collectors.toList());
        assertIterableEquals(expectedFriendList, actualFriendListContent);*/
}
