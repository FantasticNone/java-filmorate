package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(User user);
    List<User> getAllUsers();
    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    User getUserById(Long userId);
}
