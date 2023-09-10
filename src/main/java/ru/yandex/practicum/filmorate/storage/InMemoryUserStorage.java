package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.model.User.userId;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) throws BadRequestException {
        long userId = userId();
        user.setId(userId);
        setNameIfEmpty(user);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        User updatedUser = users.get(user.getId());

        if (updatedUser == null) {
            throw new NotFoundException("Пользователь по id:" + user.getId() + " не найден.");
        }

        setNameIfEmpty(user);

        updatedUser.setEmail(user.getEmail());
        updatedUser.setLogin(user.getLogin());
        updatedUser.setName(user.getName());
        updatedUser.setBirthday(user.getBirthday());

        return updatedUser;
    }

    @Override
    public void deleteUser(User user) {
        User deletedUser = users.get(user.getId());

        if (deletedUser == null) {
            throw new NotFoundException("Пользователь с id: " + user.getId() + " не найден.");
        }
        users.remove(deletedUser.getId());
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>(users.values());
        return userList;
    }

    @Override
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
        for (User user : users.values()) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            //log.info("Имя заменено на логин: {}", user.getLogin());
        }
    }
}
