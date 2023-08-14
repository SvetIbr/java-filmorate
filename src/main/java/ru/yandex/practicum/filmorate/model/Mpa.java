package ru.yandex.practicum.filmorate.model;

import lombok.*;

/**
 * Класс рейтинга со свойствами <b>id</b> и <b>name</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
@Data
public class Mpa {
    /**
     * Поле идентификатор
     */
    private Long id;
    /**
     * Поле имя
     */
    private String name;
    /**
     * Поле описание
     */
    private String description;
}
