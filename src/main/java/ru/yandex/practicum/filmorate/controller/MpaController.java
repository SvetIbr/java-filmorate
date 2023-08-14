package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

/**
 * Класс контроллера для работы с запросами к сервису рейтингов
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    /**
     * Поле сервис для работы с хранилищем рейтингов
     */
    private final MpaService service;

    /**
     * Метод получения всего списка рейтингов через запрос
     *
     * @return список всех рейтингов
     */
    @GetMapping
    public List<Mpa> findAll() {
        return service.findAllMpa();
    }

    /**
     * Метод получения рейтинга по идентификатору из хранилища сервиса через запрос
     *
     * @param id идентификатор
     * @return копию объекта mpa с указанным идентификатором
     */
    @GetMapping("/{id}")
    public Mpa findMpaById(@PathVariable Long id) {
        return service.findMpaById(id);
    }
}
