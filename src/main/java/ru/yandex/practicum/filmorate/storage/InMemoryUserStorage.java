package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();

    private static long userId = 1;

    @Override
    public User createUser(User user) {

        user.setId(generateUserId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        User updatedUser = users.get(user.getId());

        if (updatedUser == null) {
            throw new NotFoundException("Пользователь по id:" + user.getId() + " не найден.");
        }

        updatedUser.setEmail(user.getEmail());
        updatedUser.setLogin(user.getLogin());
        updatedUser.setName(user.getName());
        updatedUser.setBirthday(user.getBirthday());

        return updatedUser;
    }

    @Override
    public void deleteUser(long id) {
        User deletedUser = users.get(id);

        if (deletedUser == null) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден.");
        }
        users.remove(deletedUser.getId());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>(users.values());
        return userList;
    }

    private synchronized long generateUserId() {
        return userId++;
    }
}
