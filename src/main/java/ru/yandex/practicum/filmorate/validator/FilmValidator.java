package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

/**
 * Класс валидатора для проверки объектов film
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@Component
public class FilmValidator {
    /**
     * Поле отсчет даты релиза
     */
    private static final LocalDate START_OF_ANY_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    /**
     * Метод проверки фильма на отсутствие необходимых заполненных полей и соответствие требованиям даты релиза
     *
     * @param film {@link Film}
     * @throws ValidationException с сообщением о причине возникновения исключения,
     *                             если проверяемый объект не соответствует
     */
    public void validate(Film film) {
        if (film == null) {
            throw new ValidationException("Данные для заполнения фильма отсутствуют");
        }
        if (film.getName() == null
                || film.getName().isBlank()
                || film.getName().isEmpty()) {
            log.error("Название фильма пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isEmpty()) {
            log.error("Описание фильма пустое");
            throw new ValidationException("Добавьте описание фильма");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма больше 200 символов");
            throw new ValidationException("Длина описания фильма не может быть более 200 символов");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма меньше или равна 0");
            throw new ValidationException("Продолжительность фильма не может быть меньше или равна 0");
        }
        if (film.getReleaseDate() == null) {
            log.error("Дата релиза фильма null");
            throw new ValidationException("Дата релиза не указана");
        }
        if (film.getReleaseDate().isBefore(START_OF_ANY_RELEASE_DATE)) {
            log.error("Дата релиза фильма раньше 28 декабря 1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
