package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Film> findAllFilms() {
        String sql = "SELECT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name r_name " +
                "FROM films AS f " +
                "JOIN ratings AS r ON f.rating_id = r.rating_id " +
                "ORDER BY f.film_id";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating_id", film.getMpa().getId());

        film.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return film;
    }

    public Film updateFilm(Film film) {
        String sql =
                "UPDATE films SET name = ?, description = ?," +
                        " release_date = ?, duration = ?, rating_id = ? " +
                        "WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());

        return film;
    }

    public void deleteAllFilms() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
        String sql1 = "ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1";
        jdbcTemplate.update(sql1);
    }

    public Film getFilmById(Long id) {
        String sql = "SELECT f.film_id, f.name, f.description, " +
                "f.release_date, f.duration, f.rating_id, r.name r_name " +
                "FROM films f " +
                "JOIN ratings r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id = ?";
        List<Film> result = jdbcTemplate.query(sql, this::makeFilm, id);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public void addLikeToFilm(Long idFilm, Long idUser) {
        String sql = "INSERT INTO films_likes (film_id, user_id) VALUES(?, ?)";
        jdbcTemplate.update(sql, idFilm, idUser);
    }

    public void deleteLikeFromFilm(Long idFilm, Long idUser) {
        String sql = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, idFilm, idUser);
    }

    public List<Film> getPopularFilm(Integer count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "FROM films AS f " +
                "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) " +
                "DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    public Set<Long> getLikesByFilm(Film film) {
        String sql = "SELECT user_id " +
                "FROM films_likes " +
                "WHERE film_id = ? " +
                "ORDER BY user_id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, film.getId());
        Set<Long> likes = new HashSet<>();
        while (sqlRowSet.next()) {
            likes.add(sqlRowSet.getLong("user_id"));
        }
        return likes;
    }

    public boolean checkLike(Long idFilm, Long idUser) {
        String sql = "SELECT * FROM films_likes WHERE film_id = ? AND user_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, idFilm, idUser);
        return rows.next();
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("film_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getLong("rating_id"));
        Film film = new Film(name, description, releaseDate, duration, mpa);
        film.setId(id);
        return film;
    }
}
