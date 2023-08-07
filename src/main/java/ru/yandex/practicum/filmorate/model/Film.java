package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * Класс фильма со свойствами <b>id</b>, <b>name</b>, <b>description</b>,
 * <b>releaseDate</b>, <b>duration</b> и <b>likes</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
@Data
@AllArgsConstructor
public class Film {
    private long id;
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
    private Set<Long> likes;
    /**
     * Поле рейтинг фильма
     */
    private final Rating rating;

    /**
     * Поле список жанров фильма
     */
    private final Set<Genre> genres;
}
