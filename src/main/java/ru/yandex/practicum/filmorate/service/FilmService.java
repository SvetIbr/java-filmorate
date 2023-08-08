package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.HashSet;
import java.util.List;

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
    private final GenreStorage genreStorage;

    public FilmService(@Qualifier("filmDbStorage")FilmStorage storage,
                       @Qualifier("userDbStorage")UserStorage userStorage,
                       FilmValidator validator,
                       GenreStorage genreStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.validator = validator;
        this.genreStorage = genreStorage;
    }

    /**
     * Метод получения всего списка фильмов из хранилища
     *
     * @return список всех фильмов
     */
    public List<Film> findAllFilms() {
        List<Film> films = storage.findAllFilms();
        films.forEach(this::loadGenresAndLikes);
        return films;
    }

    private void loadGenresAndLikes(Film film) {
        film.setGenres(genreStorage.getGenresByFilm(film));
        film.setLikes(storage.loadLikes(film));
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
        film = storage.createFilm(film);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
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
        Film checkFilm = storage.getFilmById(checkId);
        if (checkFilm == null) {
            throw new NotFoundException("Фильм с идентификатором " + checkId + " не найден");
        }
        validator.validate(film);
        film = storage.updateFilm(film);
        film.setLikes(storage.loadLikes(film));
        film.setGenres(genreStorage.getGenresByFilm(film));
        log.info("Обновлен пользователь: " + film);
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
        film.setLikes(storage.loadLikes(film));
        film.setGenres(genreStorage.getGenresByFilm(film));
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
        User user = userStorage.getUserById(idUser);
        if (user == null) {
            throw new NotFoundException("Пользователь с идентификатором " + idUser + " не найден");
        }
        Film film = storage.getFilmById(idFilm);
        if (film == null) {
            throw new NotFoundException("Фильм с идентификатором " + idFilm + " не найден");
        }
        // Если пользователь уже ставил лайк
        if (storage.checkLike(idFilm, idUser)) {
            log.warn("Пользователь " + idUser + " уже поставил лайк фильму " + idFilm);
            return;
        }
        // Если еще не ставил
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
        User user = userStorage.getUserById(idUser);
        if (user == null) {
            throw new NotFoundException("Пользователь с идентификатором " + idUser + " не найден");
        }
        Film film = storage.getFilmById(idFilm);
        if (film == null) {
            throw new NotFoundException("Фильм с идентификатором " + idFilm + " не найден");
        }
        // Если пользователь еще не ставил лайк
        if (!storage.checkLike(idFilm, idUser)) {
            log.warn("Пользователь " + idUser + " уже не ставил лайк фильму " + idFilm);
            return;
        }
        // Если лайк поставил
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
        return storage.getPopularFilm(count);
    }
}
