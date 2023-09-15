package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user != null && friend != null) {
            user.addFriend(friendId);
            friend.addFriend(userId);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user != null && friend != null) {
            user.removeFriend(friendId);
            friend.removeFriend(userId);
        }
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь по id: " + userId + " не найден."));
    }

    public List<User> getFriends(Long userId) throws NotFoundException {
        User user = getUserById(userId);
        if (user != null) {
            List<Long> friendIds = new ArrayList<>(user.getFriends());
            return friendIds.stream()
                    .map(friendId -> getUserById(friendId))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Пользователь по id: " + userId + " не найден.");
        }
    }

    public List<User> getCommonFriends(Long userId, Long otherId) throws NotFoundException {
        User user1 = getUserById(userId);
        User user2 = getUserById(otherId);

        if (user1 != null && user2 != null) {
            List<Long> user1FriendIds = new ArrayList<>(user1.getFriends());
            List<Long> user2FriendIds = new ArrayList<>(user2.getFriends());

            return user1FriendIds.stream()
                    .filter(user2FriendIds::contains)
                    .map(friendId -> getUserById(friendId))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Пользователь по id: " + userId + " или " + otherId + " не найден.");
        }
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
