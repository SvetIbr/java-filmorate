package ru.yandex.practicum.filmorate.model;

/**
 * Класс ответов контроллера при возникновении исключений с полем <b>error</b>
 *
 * @version 1.0
 * @autor Светлана Ибраева
 */
public class ErrorResponse {
    /**
     * Поле ошибка
     */
    private final String error;

    /**
     * Конструктор - создание нового объекта
     */
    public ErrorResponse(String error) {
        this.error = error;
    }

    /**
     * Метод получения значения поля error
     *
     * @return возвращает значение поля error
     */
    public String getError() {
        return error;
    }
}
