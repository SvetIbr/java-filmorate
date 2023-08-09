package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс хранилища жанров
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
public interface GenreStorage {
    /**
     * Метод получения списка жанров фильма
     *
     * @param film {@link Film}
     * @return список жанров фильма
     */
    Set<Genre> getGenresByFilm(Film film);

    /**
     * Метод получения всего списка жанров из хранилища
     *
     * @return список всех жанров
     */
    List<Genre> findAllGenres();

    /**
     * Метод получения жанра по идентификатору из хранилища сервиса
     *
     * @param id идентификатор
     * @return копию объекта genre с указанным идентификатором
     */
    Genre findGenreById(Long id);
}
