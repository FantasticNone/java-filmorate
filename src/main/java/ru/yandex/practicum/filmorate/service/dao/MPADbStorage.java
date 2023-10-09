package ru.yandex.practicum.filmorate.service.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MPADbStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MPA> getAllMPA() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, this::rowMapperForRating);
    }

    @Override
    public MPA getMPAById(int id) {
        if (!existsId(id)) {
            throw new NotFoundException("MPA с ID " + id + " не найден.");
        }
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, this::rowMapperForRating);
    }


    private MPA rowMapperForRating(ResultSet rs, int rowNum) throws SQLException {
        return MPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }

    private boolean existsId(int id) {
        String sql = "SELECT COUNT(*) FROM mpa WHERE mpa_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
        return count > 0;
    }
}
