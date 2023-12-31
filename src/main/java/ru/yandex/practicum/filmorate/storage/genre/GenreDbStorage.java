package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Genre> getGenresByFilm(Film film) {
        String sql = "SELECT g.GENRE_ID, g.NAME FROM GENRES g NATURAL JOIN FILMS_GENRES fg WHERE fg.FILM_ID = ?";
        List<Genre> genres = jdbcTemplate.query(sql, this::makeGenre, film.getId());
        genres.sort(Comparator.comparing(Genre::getId));
        return new HashSet<>(genres);
    }

    public List<Genre> findAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    public Genre findGenreById(Long id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> result = jdbcTemplate.query(sql, this::makeGenre, id);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public void updateGenresByFilm(Film film) {
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", film.getId());
        addGenresToFilm(film);
    }

    public void addGenresToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genre.getId());
        }
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getLong("GENRE_ID"),
                resultSet.getString("NAME"));
    }
}
