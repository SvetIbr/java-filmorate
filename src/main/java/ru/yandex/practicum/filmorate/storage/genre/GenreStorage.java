package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Set<Genre> getGenresByFilm(Film film);

    List<Genre> findAllGenres();
    Genre findGenreById(Long id);
}
