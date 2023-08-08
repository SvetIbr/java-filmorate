package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс хранилища пользователей
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
public interface FilmStorage {
    /**
     * Метод получения всего списка фильмов из хранилища
     *
     * @return список всех фильмов
     */
    List<Film> findAllFilms();

    /**
     * Метод добавления фильма в хранилище
     *
     * @param film {@link Film}
     * @return копию объекта film с добавленным id
     */
    Film createFilm(Film film);

    /**
     * Метод обновления фильма в хранилище
     *
     * @param film {@link Film}
     * @return копию объекта film с обновленными полями
     */
    Film updateFilm(Film film);

    /**
     * Метод очищения списка всех фильмов в хранилище
     */
    void deleteAllFilms();

    /**
     * Метод получения фильма по идентификатору из хранилища
     *
     * @param id идентификатор
     * @return копию объекта film с указанным идентификатором
     */
    Film getFilmById(Long id);

    /**
     * Метод добавления лайка в список лайков фильма из хранилища
     *
     * @param idFilm,idUser идентификатор фильма, которому добавляется лайк,
     *                      идентификатор пользователя user,
     *                      который ставит лайк {@link ru.yandex.practicum.filmorate.model.User}
     */
    void addLikeToFilm(Long idFilm, Long idUser);

    /**
     * Метод удаления добавленного лайка у фильма из хранилища
     *
     * @param idFilm,idUser идентификатор фильма, у которого удаляют лайк,
     *                      идентификатор пользователя user,
     *                      который удаляет лайк {@link ru.yandex.practicum.filmorate.model.User}
     */
    void deleteLikeFromFilm(Long idFilm, Long idUser);

    /**
     * Метод получения списка самых популярных фильмов из хранилища
     *
     * @param count количество первых по популярности фильмов в списке
     * @return список фильмов, сформированных по количеству лайков
     */
    List<Film> getPopularFilm(Integer count);

    Set<Long> loadLikes(Film film);
    boolean checkLike(Long idFilm, Long idUser);
}
