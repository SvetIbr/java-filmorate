package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;


/**
 * Класс контроллера для работы с запросами к сервису жанров
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    /**
     * Поле сервис для работы с хранилищем жанров
     */
    private final GenreService service;

    /**
     * Метод получения всего списка жанров через запрос
     *
     * @return список всех жанров
     */
    @GetMapping
    public List<Genre> findAll() {
        return service.findAllGenres();
    }

    /**
     * Метод получения жанра по идентификатору из хранилища сервиса через запрос
     *
     * @param id идентификатор
     * @return копию объекта genre с указанным идентификатором
     */
    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable Long id) {
        return service.findGenreById(id);
    }
}
