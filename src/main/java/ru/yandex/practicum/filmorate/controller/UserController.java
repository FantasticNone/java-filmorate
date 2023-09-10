package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;

    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws BadRequestException {

        log.info("Создан новый пользователь: {}", user);
        return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @PathVariable long id, @RequestBody User user) throws NotFoundException {

        user.setId(id);
        log.info("Обновлен пользователь с id: {}", id);
        return userStorage.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Valid @PathVariable long id, @RequestBody User user) throws NotFoundException {

        user.setId(id);
        log.info("Удален пользователь с id: {}", id);
        userStorage.deleteUser(user);
    }
}

