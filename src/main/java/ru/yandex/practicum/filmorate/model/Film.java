package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс фильма со свойствами <b>id</b>, <b>name</b>, <b>description</b>,
 * <b>releaseDate</b>, <b>duration</b>, <b>likes</b>, <b>rating</b> и <b>genres</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
@Data
public class Film {
    /**
     * Поле идентификатор
     */
    private Long id;
    /**
     * Поле название
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
    private Set<Genre> genres;

    /**
     * Конструктор - создание нового объекта с определенными значениями
     *
     * @param name        - название
     * @param description - описание
     * @param releaseDate - дата выхода
     * @param duration    - продолжительность
     * @param rating      - рейтинг
     */
    public Film(String name, String description, LocalDate releaseDate, long duration, Rating rating) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rating = rating;
        this.genres = new HashSet<>();
        this.likes = new HashSet<>();
    }
}
