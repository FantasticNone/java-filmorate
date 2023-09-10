package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws BadRequestException {

        log.info("Создан новый пользователь: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @PathVariable long id, @RequestBody User user) throws NotFoundException {

        user.setId(id);
        log.info("Обновлен пользователь с id: {}", id);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@Valid @PathVariable long id) throws NotFoundException {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Valid @PathVariable long id, @RequestBody User user) throws NotFoundException {

        user.setId(id);
        log.info("Удален пользователь с id: {}", id);
        userService.deleteUser(user);
    }
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@Valid @PathVariable long id, @Valid @PathVariable long friendId) {
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@Valid @PathVariable long id, @Valid @PathVariable long friendId) {
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@Valid @PathVariable long id) throws NotFoundException {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@Valid @PathVariable long id, @Valid @PathVariable long otherId) throws NotFoundException {
        return userService.getCommonFriends(id, otherId);
    }
}

