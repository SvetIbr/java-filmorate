package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

/**
 * Класс обработчика возникающих исключений
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@RestControllerAdvice
public class ErrorHandler {
    /**
     * Метод обработки исключений при валидации объектов user{@link User}, film{@link Film}
     *
     * @return сообщение с описанием причины возникновения ошибки и статусом 400
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    /**
     * Метод обработки исключений при возникновении непредвиденного исключения
     *
     * @return стандартное сообщение и статус 500
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }

    /**
     * Метод обработки исключений при отсутствии
     * искомых объектов user{@link User} и film{@link Film} по идентификатору
     *
     * @return сообщение с описанием причины возникновения ошибки и статусом 404
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }
}
