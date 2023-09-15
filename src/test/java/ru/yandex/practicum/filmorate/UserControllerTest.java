package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

class UserControllerTest {

    private UserController userController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.getAllUsers();

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createUser_InvalidEmail_ReturnsBadRequest() {

        User user = new User("", "test", "test", LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Email не может быть пустым", violation.getMessage());

    }

    @Test
    void createUser_InvalidLogin_ReturnsBadRequest() {
        User user = new User("test@example.com", "test test", "test", LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не может содержать пробелы", violation.getMessage());
    }


    @Test
    void createUser_ValidUser_ReturnsOk() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setName("test");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        try {
            User createdUser = userController.createUser(user);
            assertEquals(user, createdUser);
        } catch (ValidationException ex) {
            throw new AssertionError("Unexpected BadRequestException");
        }
    }
}

