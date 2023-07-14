package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAllFilms();
    Film createFilm(Film film);
    Film updateFilm(Film film);
    void deleteAllFilms();
    Film getFilmById(Long id);
    void addLikeToFilm(Long idFilm, Long idUser);
    void deleteLikeFromFilm(Long idFilm, Long idUser);
    List<Film> getPopularFilm(Integer count);
}
