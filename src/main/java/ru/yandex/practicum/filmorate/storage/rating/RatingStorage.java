package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

/**
 * Интерфейс хранилища рейтингов
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
public interface RatingStorage {
    /**
     * Метод получения всего списка рейтингов из хранилища
     *
     * @return список всех рейтингов
     */
    List<Rating> findAllRatings();

    /**
     * Метод получения рейтинга по идентификатору из хранилища сервиса
     *
     * @param id идентификатор
     * @return копию объекта rating с указанным идентификатором
     */
    Rating findRatingById(Long id);
}
