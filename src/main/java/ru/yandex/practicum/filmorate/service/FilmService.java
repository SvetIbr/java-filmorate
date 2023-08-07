package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;

/**
 * Класс сервиса для работы с хранилищем фильмов
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    /**
     * Поле хранилище фильмов
     */
    private final FilmStorage inMemoryFilmStorage;
    /**
     * Поле хранилище пользователей
     */
    private final UserStorage inMemoryUserStorage;
    /**
     * Поле валидатор
     */
    private final FilmValidator validator;

    /**
     * Метод получения всего списка фильмов из хранилища
     *
     * @return список всех фильмов
     */
    public List<Film> findAllFilms() {
        return inMemoryFilmStorage.findAllFilms();
    }

    /**
     * Метод добавления фильма в хранилище
     *
     * @param film {@link Film}
     * @return копию объекта film с добавленным id
     * @throws ValidationException если объект не прошел валидацию
     */
    public Film createFilm(Film film) {
//        if (film.getId() == null) {
//            film.setId(0L);
//        }
        if (film.getId() > 0) {
            log.error("Попытка добавить фильм со своим идентификатором " +
                    "(при создании генерируется автоматически)");
            throw new ValidationException("Фильм не должен иметь идентификатора " +
                    "(при создании генерируется автоматически)");
        }
        validator.validate(film);
        return inMemoryFilmStorage.createFilm(film);
    }

    /**
     * Метод обновления фильма в хранилище сервиса
     *
     * @param film {@link Film}
     * @return копию объекта film с обновленными полями
     */
    public Film updateFilm(Film film) {
        validator.validate(film);
        return inMemoryFilmStorage.updateFilm(film);
    }

    /**
     * Метод очищения списка всех фильмов в хранилище сервиса
     */
    public void deleteAllFilms() {
        inMemoryFilmStorage.deleteAllFilms();
    }

    /**
     * Метод получения фильма по идентификатору из хранилища сервиса
     *
     * @param id идентификатор
     * @return копию объекта film с указанным идентификатором
     */
    public Film getFilmById(Long id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    /**
     * Метод добавления лайка в список лайков фильма
     *
     * @param idFilm,idUser идентификатор фильма, которому добавляется лайк,
     *                      идентификатор пользователя user,
     *                      который ставит лайк {@link ru.yandex.practicum.filmorate.model.User}
     */
    public void addLikeToFilm(Long idFilm, Long idUser) {
        inMemoryUserStorage.getUserById(idUser);
        inMemoryFilmStorage.addLikeToFilm(idFilm, idUser);
    }

    /**
     * Метод удаления добавленного лайка у фильма
     *
     * @param idFilm,idUser идентификатор фильма, у которого удаляют лайк,
     *                      идентификатор пользователя user,
     *                      который удаляет лайк {@link ru.yandex.practicum.filmorate.model.User}
     */
    public void deleteLikeFromFilm(Long idFilm, Long idUser) {
        inMemoryUserStorage.getUserById(idUser);
        inMemoryFilmStorage.deleteLikeFromFilm(idFilm, idUser);
    }

    /**
     * Метод получения списка самых популярных фильмов из хранилища сервиса
     *
     * @param count количество первых по популярности фильмов в списке
     * @return список фильмов, сформированных по количеству лайков
     */
    public List<Film> getPopularFilm(Integer count) {
        return inMemoryFilmStorage.getPopularFilm(count);
    }
}
