package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.List;

/**
 * Класс контроллера для работы с фильмами
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    /**
     * Метод получения всего списка фильмов из памяти контроллера через запрос
     *
     * @return список всех фильмов
     */
    @GetMapping
    public List<Film> findAll() {
        return service.findAllFilms();
    }

    /**
     * Метод добавления объекта film в память контроллера через запрос
     *
     * @param film фильм
     * @return копию объекта фильм с добавленным id
     * @throws ValidationException если объект не прошел валидацию
     */
    @PostMapping
    public Film create(@RequestBody Film film) {
        return service.createFilm(film);
    }

    /**
     * Метод обновления объекта film через запрос
     *
     * @param film фильм
     * @return копию объекта film с обновленными полями и код ответа API
     * @throws ValidationException если объект не прошел валидацию
     */
    @PutMapping
    public Film update(@RequestBody Film film) {
        return service.updateFilm(film);
    }

    /**
     * Метод очищения списка всех фильмов в  памяти контроллера через запрос
     *
     * @return код ответа API
     */
    @DeleteMapping
    public HttpStatus deleteAll() {
        service.deleteAllFilms();
        return (HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable Long id) {
        return service.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable Long id, Long userId) {
        service.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable Long id, Long userId) {
        service.deleteLikeFromFilm(id, userId);
    }

    @GetMapping(value = {"/popular?count={count}", "/popular"})
    public List<Film> getPopularFilm(@PathVariable(required = false) Integer count) {
        if (count == null) {
            count = 10;
        }
        return service.getPopularFilm(count);
    }

}
