package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс пользователя со свойствами <b>id</b>, <b>email</b>, <b>login</b>,
 * <b>name</b>, <b>birthday</b> и <b>friends</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
@Data
//@AllArgsConstructor
public class User {
    /**
     * Поле идентификатор
     */
    private Long id;
    /**
     * Поле электронная почта
     */
    private final String email;
    /**
     * Поле логин
     */
    private final String login;
    /**
     * Поле никнейм
     */
    private String name;
    /**
     * Поле дата рождения
     */
    private final LocalDate birthday;
    /**
     * Поле список друзей
     */
    private Set<Long> friends;

    /**
     * Конструктор - создание нового объекта с определенными значениями
     *
     * @param email    - электронная почта
     * @param login    - логин
     * @param name     - никнейм
     * @param birthday - дата рождения
     */
    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }
}
