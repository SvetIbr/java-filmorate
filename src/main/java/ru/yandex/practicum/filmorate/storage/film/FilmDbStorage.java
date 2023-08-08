package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
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

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setRating(new Rating(resultSet.getLong("rating_id"), resultSet.getString("name")));
        return film;
    }

    public Set<Long> loadLikes(Film film) {
        String sql = "SELECT USER_ID FROM FILMS_LIKES WHERE FILM_ID = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, film.getId());
        Set<Long> likes = new HashSet<>();
        while (sqlRowSet.next()) {
            likes.add(sqlRowSet.getLong("USER_ID"));
        }
        return likes;
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        values.put("RATING_ID", film.getRating().getId());

        film.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql =
                "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? " +
                        "WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRating().getId(), film.getId());

        return film;
    }

    @Override
    public void deleteAllFilms() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    public Film getFilmById(Long id) {
        String sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                        "WHERE f.FILM_ID = ?";
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

    public boolean checkLike(Long idFilm, Long idUser) {
        String sql = "SELECT * FROM films_likes WHERE film_id = ? AND user_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, idFilm, idUser);
        return rows.next();
    }

    public void deleteLikeFromFilm(Long idFilm, Long idUser) {
        String sql = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, idFilm, idUser);
    }

    public List<Film> getPopularFilm(Integer count) {
        String sql = "SELECT * FROM films WHERE film_id IN " +
                "(SELECT film_id " +
                "FROM films_likes " +
                "GROUP BY film_id " +
                "ORDER BY SUM(user_id) DESC " +
                "LIMIT " + count + ")";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

}
