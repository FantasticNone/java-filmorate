package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.storage.FriendStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQueryInsert = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";

        jdbcTemplate.update(sqlQueryInsert, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sqlQueryDelete = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQueryDelete, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        String sqlQuery = "SELECT u.user_id, u.email, u.name, u.login, u.birthday " +
                "FROM users u " +
                "JOIN friends f ON f.friend_id = u.user_id " +
                "WHERE f.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        String sqlQuery = "SELECT u.user_id, u.email, u.name, u.login, u.birthday " +
                "FROM users u " +
                "JOIN friends f1 ON u.user_id = f1.friend_id " +
                "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? " +
                "AND f2.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, friendId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

}
