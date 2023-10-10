package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    List<User> getAllUsers();

    Optional<User> getUserById(Integer userId);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int otherId);

    boolean isUserExist(int id);
}
