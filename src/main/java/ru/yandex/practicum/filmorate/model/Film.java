package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * Класс фильма со свойствами <b>id</b>, <b>name</b>, <b>description</b>,
 * <b>releaseDate</b>, <b>duration</b>, <b>likes</b>, <b>rating</b> и <b>genres</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
@Data
@AllArgsConstructor
public class Film {
    /**
     * Поле идентификатор
     */
    private Long id;
    /**
     * Поле наименование
     */
    private String name;
    /**
     * Поле описание
     */
    private String description;
    /**
     * Поле дата релиза
     */
    private LocalDate releaseDate;
    /**
     * Поле продолжительность
     */
    private long duration;
    /**
     * Поле список лайков
     */
    private Set<Long> likes;
    /**
     * Поле рейтинг фильма
     */
    private Rating rating;

    /**
     * Поле список жанров фильма
     */
    private Set<Genre> genres;

    public Film() {
    }
}
