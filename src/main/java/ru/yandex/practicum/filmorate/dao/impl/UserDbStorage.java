package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sqlQuery, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            if (user.getName() != null)
                ps.setString(3, user.getName());
            else
                ps.setNull(3, Types.NULL);
            ps.setObject(4, user.getBirthday());

            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        String sqlQuery = "SELECT user_id, email, name, login, birthday FROM users " +
                "WHERE user_id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new Object[]{userId}, this::mapRowToUser));
        } catch (EmptyResultDataAccessException exc) {
            log.debug("User id - {} not found", userId);
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET email = ?," +
                "login = ?," +
                "name = ?," +
                "birthday = ?" +
                "WHERE user_id = ?";

        int rowsAffected = jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());

        if (rowsAffected == 0) {
            log.debug("Пользователь по id {} не найден.", user.getId());
            throw new NotFoundException("Пользователь не найден.");
        }
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";

        if (jdbcTemplate.update(sqlQuery, userId) == 1) {
            log.info("Пользователь по id: " + userId + " удален.");
        } else {
            log.debug("Пользователь по id {} не найден.", userId);
            throw new NotFoundException("Пользователь не найден.");
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT user_id, email, name, login, birthday FROM users";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        boolean callbackRequest = false;

        String sqlQueryGetStatus = "SELECT status FROM friends " +
                "WHERE user_id = ? AND friend_id = ?";
        String sqlQueryInsertRequest = "INSERT INTO friends (user_id, friend_id, status) " +
                "VALUES (?, ?, ?)";
        String sqlQueryUpdateRequest = "UPDATE friends SET status = true " +
                "WHERE user_id = ? AND friend_id = ?";

        try {
            callbackRequest = Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQueryGetStatus, boolean.class,
                    friendId, userId));
        } catch (EmptyResultDataAccessException exc) {
            log.debug("Встречный запрос от пользователя {} отсутствует", userId);
        }

        if (callbackRequest) {
            jdbcTemplate.update(sqlQueryUpdateRequest, userId, friendId);
            jdbcTemplate.update(sqlQueryUpdateRequest, friendId, userId);
        } else {
            jdbcTemplate.update(sqlQueryInsertRequest, userId, friendId, false);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery, userId, friendId);
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

    @Override
    public boolean isUserExist(int id) {
        String sql = "SELECT user_id FROM users WHERE user_id = ?;";

        SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id);
        return set.next();
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