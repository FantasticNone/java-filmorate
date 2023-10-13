package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    List<User> getAllUsers();

    User getUserById(Integer userId);

    boolean isUserExist(int id);
}
