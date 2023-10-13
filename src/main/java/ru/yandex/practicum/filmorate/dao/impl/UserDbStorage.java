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
import ru.yandex.practicum.filmorate.model.Film;
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
    public User getUserById(Integer userId) {
        String sqlQuery = "SELECT user_id, email, name, login, birthday FROM users " +
                "WHERE user_id = ?";

        try {

            User user = jdbcTemplate.queryForObject(sqlQuery, new Object[]{userId}, this::mapRowToUser);

            return user;
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