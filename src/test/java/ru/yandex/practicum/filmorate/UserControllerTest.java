package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        userController.getAllUsers();
    }

    @Test
    void createUser_InvalidEmail_ReturnsBadRequest() {

        User user = new User("", "test", "test", LocalDate.of(2000, 1, 1));

        ResponseEntity<?> responseEntity = userController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List);
        List<String> errors = (List<String>) responseEntity.getBody();
        assertEquals(1, errors.size());
        assertEquals("Неправильный формат электронной почты", errors.get(0));
    }

    @Test
    void createUser_InvalidLogin_ReturnsBadRequest() {

        User user = new User("test@example.com", "", "test", LocalDate.of(2000, 1, 1));

        ResponseEntity<?> responseEntity = userController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List);
        List<String> errors = (List<String>) responseEntity.getBody();
        assertEquals(1, errors.size());
        assertEquals("Логин не может быть пустым и содержать пробелы", errors.get(0));
    }

    @Test
    void createUser_ValidUser_ReturnsOk() {

        User user = new User("test@example.com", "test", "test", LocalDate.of(2000, 1, 1));

        ResponseEntity<?> responseEntity = userController.createUser(user);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof User);
        User createdUser = (User) responseEntity.getBody();
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
    }
}

