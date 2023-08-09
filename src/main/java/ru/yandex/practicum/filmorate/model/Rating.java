package ru.yandex.practicum.filmorate.model;

import lombok.*;

/**
 * Класс рейтинга со свойствами <b>id</b> и <b>name</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Rating {
    /**
     * Поле идентификатор
     */
    private Long id;
    /**
     * Поле имя
     */
    private String name;
}
