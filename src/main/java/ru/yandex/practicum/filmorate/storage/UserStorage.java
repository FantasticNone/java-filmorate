package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> createUser(User user);

    Optional<User> updateUser(User user);

    void deleteUser(long id);

    List<User> getAllUsers();

    Optional<User> getUserById(Long userId);

}
