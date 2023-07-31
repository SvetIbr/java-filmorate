package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.List;

/**
 * Класс контроллера для работы с запросами к сервису фильмов
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    /**
     * Поле сервис для работы с хранилищем фильмов
     */
    private final FilmService service;

    /**
     * Метод получения всего списка фильмов через запрос
     *
     * @return список всех фильмов
     */
    @GetMapping
    public List<Film> findAll() {
        return service.findAllFilms();
    }

    /**
     * Метод добавления фильма в хранилище сервиса через запрос
     *
     * @param film {@link Film}
     * @return копию объекта film с добавленным id код ответа API 201
     */
    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film film) {
        return new ResponseEntity<>(service.createFilm(film), HttpStatus.CREATED);
    }

    /**
     * Метод обновления фильма в хранилище сервиса через запрос
     *
     * @param film {@link Film}
     * @return копию объекта film с обновленными полями
     */
    @PutMapping
    public Film update(@RequestBody Film film) {
        return service.updateFilm(film);
    }

    /**
     * Метод очищения списка всех фильмов в хранилище сервиса через запрос
     *
     * @return код ответа API
     */
    @DeleteMapping
    public HttpStatus deleteAll() {
        service.deleteAllFilms();
        return (HttpStatus.OK);
    }

    /**
     * Метод получения фильма по идентификатору из хранилища сервиса через запрос
     *
     * @param id идентификатор
     * @return копию объекта film с указанным идентификатором
     */
    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable Long id) {
        return service.getFilmById(id);
    }

    /**
     * Метод добавления лайка фильму через запрос
     *
     * @param id,userId идентификатор фильма, идентификатор пользователя user,
     *                  который ставит лайк {@link ru.yandex.practicum.filmorate.model.User}
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        service.addLikeToFilm(id, userId);
    }

    /**
     * Метод удаления добавленного лайка фильму через запрос
     *
     * @param id,userId идентификатор фильма, идентификатор пользователя user,
     *                  который удаляет свой лайк {@link ru.yandex.practicum.filmorate.model.User}
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLikeFromFilm(id, userId);
    }

    /**
     * Метод получения списка самых популярных фильмов из хранилища сервиса через запрос
     *
     * @param count количество первых по популярности фильмов в списке,
     *              если count не указано в запросе, по умолчанию count становится 10
     * @return список фильмов, сформированных по количеству лайков
     */
    @GetMapping(value = {"/popular?count={count}", "/popular"})
    public List<Film> getPopularFilm(@RequestParam(required = false) Integer count) {
        if (count == null) {
            count = 10;
        }
        return service.getPopularFilm(count);
    }
}
