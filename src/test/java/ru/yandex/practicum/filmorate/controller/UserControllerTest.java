package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

class UserControllerTest {

    private UserController userController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory valid = Validation.buildDefaultValidatorFactory();


        validator = valid.getValidator();
    }

    @Test
    void createUser_InvalidEmail_ReturnsBadRequest() {
        User user = User.builder()
                .email("")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Email не может быть пустым", violation.getMessage());

    }

    @Test
    void createUser_InvalidLogin_ReturnsBadRequest() {
        User user = User.builder()
                .email("test@example.com")
                .login("test test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не может содержать пробелы", violation.getMessage());
    }


    @Test
    void createUser_ValidUser_ReturnsOk() {

        User user = User.builder()
                .email("test@example.com")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        try {
            User createdUser = userController.createUser(user);
            assertEquals(user, createdUser);
        } catch (ValidationException ex) {
            throw new AssertionError("Unexpected BadRequestException");
        }
    }
}

