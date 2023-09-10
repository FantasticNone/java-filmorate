package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) throws BadRequestException {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) throws NotFoundException {
        return userStorage.updateUser(user);
    }

    public void deleteUser(User user) {
        userStorage.deleteUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(User user1, User user2) {
        List<Long> user1FriendIds = new ArrayList<>(user1.getFriends());
        List<Long> user2FriendIds = new ArrayList<>(user2.getFriends());

        return user1FriendIds.stream()
                .filter(user2FriendIds::contains)
                .map(userId -> userStorage.getUserById(userId))
                .collect(Collectors.toList());
    }
}
