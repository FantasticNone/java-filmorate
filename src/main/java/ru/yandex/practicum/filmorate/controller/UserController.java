package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private List<User> users = new ArrayList<>();

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        List<String> errors = new ArrayList<>();

        try {
            int generatedId = User.usersId();
            user.setId(generatedId);

            if (user.getEmail().isEmpty() || !user.getEmail().contains("@"))
                errors.add("Неправильный формат электронной почты");
            if (user.getLogin().isEmpty() || user.getLogin().contains(" "))
                errors.add("Логин не может быть пустым и содержать пробелы");
            if (user.getName().isEmpty())
                errors.add("Имя не может быть пустым");
            if (user.getBirthday().isAfter(LocalDate.now()))
                errors.add("Дата рождения не может быть в будущем");

            if (!errors.isEmpty())
                throw new ValidationException(errors);

            users.add(user);
            log.info("Создан новый пользователь: {}", user);
            return ResponseEntity.ok(user);

        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrors());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
        List<String> errors = new ArrayList<>();

        try {
            User updatedUser = users.stream()
                    .filter(u -> u.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с id:" + id + "не найден."));
            if (user.getEmail().isEmpty() || !user.getEmail().contains("@"))
                errors.add("Неправильный формат электронной почты");
            if (user.getLogin().isEmpty() || user.getLogin().contains(" "))
                errors.add("Логин не может быть пустым и содержать пробелы");
            if (user.getName().isEmpty())
                user.setName(user.getLogin());
            if (user.getBirthday().isAfter(LocalDate.now()))
                errors.add("Дата рождения не может быть в будущем");

            if (!errors.isEmpty())
                throw new ValidationException(errors);

            updatedUser.setEmail(user.getEmail());
            updatedUser.setLogin(user.getLogin());
            updatedUser.setName(user.getName());
            updatedUser.setBirthday(user.getBirthday());
            log.info("Обновлен пользователь с id: {}", id);

            return ResponseEntity.ok(updatedUser);
        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrors());
        } catch (IllegalArgumentException ex) {
            log.error("Ошибка при обновлении пользователя: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUserUnknown(@RequestBody User user) {
        List<String> errors = new ArrayList<>();

        try {

            if (user.getId() == null)
                errors.add("id не может быть пустым");
            if (user.getEmail().isEmpty() || !user.getEmail().contains("@"))
                errors.add("Неправильный формат электронной почты");
            if (user.getLogin().isEmpty() || user.getLogin().contains(" "))
                errors.add("Логин не может быть пустым и содержать пробелы");
            if (user.getName().isEmpty())
                user.setName(user.getLogin());
            if (user.getBirthday().isAfter(LocalDate.now()))
                errors.add("Дата рождения не может быть в будущем");

            if (!errors.isEmpty())
                throw new ValidationException(errors);

            users.add(user);
            log.info("Обновлен пользователь: {}", user);

            return ResponseEntity.ok(user);
        } catch (ValidationException ex) {
            log.error("Ошибка валидации: {}", ex.getErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrors());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(users);
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

