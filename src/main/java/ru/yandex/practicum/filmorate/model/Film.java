package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

/**
 * Класс фильма со свойствами <b>id</b>, <b>name</b>, <b>description</b>, <b>releaseDate</b> и <b>duration</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {
    /**
     * Поле идентификатор
     */
    private Long id;
    /**
     * Поле наименование
     */
    private final String name;
    /**
     * Поле описание
     */
    private final String description;
    /**
     * Поле дата релиза
     */
    private final LocalDate releaseDate;
    /**
     * Поле продолжительность
     */
    private final long duration;

    /**
     * Поле список лайков
     */
    private final Set<Long> likes;
}
