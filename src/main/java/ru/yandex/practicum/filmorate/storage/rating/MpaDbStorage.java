package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> findAllMpa() {
        String sql = "SELECT * FROM ratings ORDER BY rating_id";
        return jdbcTemplate.query(sql, this::makeMpa);
    }

    public Mpa findMpaById(Long id) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";
        List<Mpa> result = jdbcTemplate.query(sql, this::makeMpa, id);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getLong("rating_id"));
        mpa.setName(resultSet.getString("name"));
        mpa.setDescription(resultSet.getString("description"));
        return mpa;
    }
}
