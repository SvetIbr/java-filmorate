package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

/**
 * Класс валидатора для проверки объектов user
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@Component
public class UserValidator {

    /**
     * Метод проверки пользователя на отсутствие или некорректное заполнение необходимых полей
     *
     * @param user {@link User}
     * @throws ValidationException с сообщением о причине возникновения исключения,
     *                             если проверяемый объект не соответствует
     */
    public void validate(User user) throws ValidationException {
        if (user == null) {
            log.error("Пользователь = null");
            throw new ValidationException("Данные для создания пользователя отсутствуют");
        }
        if (user.getEmail().isBlank()
                || user.getEmail().isEmpty()
                || !user.getEmail().contains("@")) {
            log.error("Некорректный email пользователя");
            throw new ValidationException("Email пользователя не может быть пустым " +
                    "и должен содержать \"@\"");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Логин пользователя пустой или содержит пробелы");
            throw new ValidationException("Логин пользователя не может быть пустым " +
                    "и не должен содержать пробелы");
        }
        LocalDate now = LocalDate.now();
        if (user.getBirthday().isAfter(now)) {
            log.error("Дата рождения пользователя в будущем");
            throw new ValidationException("Дата рождения пользователя не может быть в будущем");
        }
    }
}
