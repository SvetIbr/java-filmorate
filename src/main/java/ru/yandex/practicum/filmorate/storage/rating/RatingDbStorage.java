package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class RatingDbStorage implements RatingStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<Rating> findAllRatings() {
        String sql = "SELECT * FROM ratings ORDER BY rating_id";
        return jdbcTemplate.query(sql, this::makeRating);
    }
    public Rating findRatingById(Long id) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";
        List<Rating> result = jdbcTemplate.query(sql, this::makeRating, id);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    private Rating makeRating(ResultSet resultSet, int rowNum) throws SQLException {
        Rating rating = new Rating(resultSet.getLong("rating_id"), resultSet.getString("name"));
        rating.setId(resultSet.getLong("rating_id"));
        rating.setName(resultSet.getString("name"));
        return rating;
    }
}
