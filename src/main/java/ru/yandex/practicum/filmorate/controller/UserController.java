package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.model.User.userId;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws BadRequestException {
        List<String> errors = new ArrayList<>();

        try {
            int userId = userId();
            user.setId(userId);

            if (user.getName() == null)
                user.setName(user.getLogin());
            if (user.getBirthday().isAfter(LocalDate.now()))
                errors.add("Дата рождения не может быть в будущем");

            if (!errors.isEmpty())
                throw new ValidationException(errors);

            users.put(user.getId(), user);
            log.info("Создан новый пользователь: {}", user);
            return user;

        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            throw new BadRequestException(ex.getErrors());
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws BadRequestException, NotFoundException {

        User updatedUser = users.get(user.getId());

        try {
            if (!users.containsKey(user.getId()) || updatedUser == null) {
                throw new NotFoundException("Пользователь по id: " + user.getId() + " не найден.");
            }

            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                log.info("Имя заменено на логин: {}", user.getLogin());
            }

            updatedUser.setEmail(user.getEmail());
            updatedUser.setLogin(user.getLogin());
            updatedUser.setName(user.getName());
            updatedUser.setBirthday(user.getBirthday());

            users.replace(user.getId(), updatedUser);
            log.info("Обновлен пользователь с id: {}", user.getId());
            return updatedUser;

        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            throw new BadRequestException(ex.getErrors());
        } catch (NotFoundException ex) {
            log.error("Ошибка при обновлении пользователя: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>(users.values());
        return userList;
    }
}

