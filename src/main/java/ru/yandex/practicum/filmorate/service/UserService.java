package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        setNameIfEmpty(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        setNameIfEmpty(user);
        return userStorage.updateUser(user);
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user != null && friend != null) {
            userStorage.addFriend(userId,friendId);
        }
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user != null && friend != null) {
            userStorage.removeFriend(userId,friendId);
        }
    }

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь по id: " + userId + " не найден."));
    }

    public List<User> getFriends(Integer userId) throws NotFoundException {
        User user = getUserById(userId);
        if (user != null) {
            return userStorage.getFriends(userId);
        } else {
            throw new NotFoundException("Пользователь по id: " + userId + " не найден.");
        }
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private Boolean isUserExist(Integer id) {
        return userStorage.getUserById(id).isPresent();
    }
}
