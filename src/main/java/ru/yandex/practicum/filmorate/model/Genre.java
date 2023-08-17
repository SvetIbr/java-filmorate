package ru.yandex.practicum.filmorate.model;

import lombok.*;

/**
 * Класс жанра со свойствами <b>id</b> и <b>name</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
@Data
@AllArgsConstructor
public class Genre {
    /**
     * Поле идентификатор
     */
    private Long id;
    /**
     * Поле имя
     */
    private String name;
}
