package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

/**
 * Класс сервиса для работы с хранилищем фильмов
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@Service
public class FilmService {
    /**
     * Поле хранилище фильмов
     */
    private final FilmStorage storage;
    /**
     * Поле хранилище пользователей
     */
    private final UserStorage userStorage;
    /**
     * Поле валидатор
     */
    private final FilmValidator validator;
    /**
     * Поле хранилище жанров
     */
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    /**
     * Конструктор - создание нового объекта с определенными значениями
     *
     * @param storage      - хранилище фильмов
     * @param userStorage  - хранилище пользователей
     * @param validator    - валидатор фильмов
     * @param genreStorage - хранилище жанров
     * @param mpaStorage   - хранилище рейтингов
     */
    public FilmService(@Qualifier("filmDbStorage") FilmStorage storage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       FilmValidator validator,
                       GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.validator = validator;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    /**
     * Метод получения всего списка фильмов из хранилища
     *
     * @return список всех фильмов
     */
    public List<Film> findAllFilms() {
        List<Film> films = storage.findAllFilms();
        films.forEach(this::loadGenresMpaAndLikes);
        return films;
    }

    /**
     * Метод добавления фильма в хранилище
     *
     * @param film {@link Film}
     * @return копию объекта film с добавленным id
     * @throws ValidationException если объект не прошел валидацию
     */
    public Film createFilm(Film film) {
        if (film.getId() == null) {
            film.setId(0L);
        }
        if (film.getId() != null && film.getId() > 0) {
            log.error("Попытка добавить фильм со своим идентификатором " +
                    "(при создании генерируется автоматически)");
            throw new ValidationException("Фильм не должен иметь идентификатора " +
                    "(при создании генерируется автоматически)");
        }
        validator.validate(film);
        Set<Genre> genres = film.getGenres();
        film = storage.createFilm(film);
        if (!genres.isEmpty()) {
            genreStorage.addGenresToFilm(film);
        }
        loadGenresMpaAndLikes(film);
        log.info("Добавлен фильм: " + film);
        return film;
    }

    /**
     * Метод обновления фильма в хранилище сервиса
     *
     * @param film {@link Film}
     * @return копию объекта film с обновленными полями
     */
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("У фильма не хватает идентификатора для обновления");
        }
        Long checkId = film.getId();
        checkFilmId(checkId);
        validator.validate(film);
        genreStorage.updateGenresByFilm(film);
        film = storage.updateFilm(film);
        loadGenresMpaAndLikes(film);
        log.info("Обновлен фильм: " + film);
        return film;
    }

    /**
     * Метод очищения списка всех фильмов в хранилище сервиса
     */
    public void deleteAllFilms() {
        storage.deleteAllFilms();
    }

    /**
     * Метод получения фильма по идентификатору из хранилища сервиса
     *
     * @param id идентификатор
     * @return копию объекта film с указанным идентификатором
     */
    public Film getFilmById(Long id) {
        Film film = storage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с идентификатором " + id + " не найден");
        }
        loadGenresMpaAndLikes(film);
        return film;
    }

    /**
     * Метод добавления лайка в список лайков фильма
     *
     * @param idFilm,idUser идентификатор фильма, которому добавляется лайк,
     *                      идентификатор пользователя user,
     *                      который ставит лайк {@link ru.yandex.practicum.filmorate.model.User}
     */
    public void addLikeToFilm(Long idFilm, Long idUser) {
        checkUserId(idUser);
        checkFilmId(idFilm);
        // Если пользователь уже ставил лайк
        if (storage.checkLike(idFilm, idUser)) {
            log.warn("Пользователь " + idUser + " уже поставил лайк фильму " + idFilm);
            return;
        }
        // Если пользователь еще не ставил лайк
        if (!storage.checkLike(idFilm, idUser)) {
            storage.addLikeToFilm(idFilm, idUser);
            log.info("Пользователь " + idUser + " поставил лайк фильму " + idFilm);
        }
    }

    /**
     * Метод удаления добавленного лайка у фильма
     *
     * @param idFilm,idUser идентификатор фильма, у которого удаляют лайк,
     *                      идентификатор пользователя user,
     *                      который удаляет лайк {@link ru.yandex.practicum.filmorate.model.User}
     */
    public void deleteLikeFromFilm(Long idFilm, Long idUser) {
        checkUserId(idUser);
        checkFilmId(idFilm);
        // Если пользователь еще не ставил лайк
        if (!storage.checkLike(idFilm, idUser)) {
            log.warn("Пользователь " + idUser + " уже не ставил лайк фильму " + idFilm);
            return;
        }
        // Если пользватель уже ставил лайк
        if (storage.checkLike(idFilm, idUser)) {
            storage.deleteLikeFromFilm(idFilm, idUser);
            log.info("Пользователь " + idUser + " отменил свой лайк фильму " + idFilm);
        }
    }

    /**
     * Метод получения списка самых популярных фильмов из хранилища сервиса
     *
     * @param count количество первых по популярности фильмов в списке
     * @return список фильмов, сформированных по количеству лайков
     */
    public List<Film> getPopularFilm(Integer count) {
        List<Film> films = storage.getPopularFilm(count);
        films.forEach(this::loadGenresMpaAndLikes);
        return films;
    }

    /**
     * Метод заполнения списков жанров и лайков фильма film
     *
     * @param film {@link Film}
     */
    private void loadGenresMpaAndLikes(Film film) {
        film.setGenres(genreStorage.getGenresByFilm(film));
        film.setLikes(storage.getLikesByFilm(film));
        film.setMpa(mpaStorage.findMpaById(film.getMpa().getId()));
    }

    /**
     * Метод проверки наличия в хранилище фильмов фильма по идентификатору
     *
     * @param id идентификатор
     */
    private void checkFilmId(Long id) {
        Film film = storage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с идентификатором " + id + " не найден");
        }
    }

    /**
     * Метод проверки наличия в хранилище пользователей пользователя по идентификатору
     *
     * @param id идентификатор
     */
    private void checkUserId(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
    }
}
