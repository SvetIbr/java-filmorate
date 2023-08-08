package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

/**
 * Класс ответов контроллера при возникновении исключений с полем <b>error</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
@Getter
public class ErrorResponse {
    /**
     * Поле ошибка
     * -- GETTER --
     *  Метод получения значения поля error
     *
     */
    private final String error;

    /**
     * Конструктор - создание нового объекта
     */
    public ErrorResponse(String error) {
        this.error = error;
    }

}
