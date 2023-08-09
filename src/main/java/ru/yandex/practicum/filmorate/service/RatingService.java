package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

/**
 * Класс сервиса для работы с хранилищем рейтингов
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Service
public class RatingService {
    /**
     * Поле хранилище рейтингов
     */
    private final RatingStorage storage;

    /**
     * Конструктор - создание нового объекта с определенными значениями
     *
     * @param storage - хранилище рейтингов
     */
    @Autowired
    public RatingService(RatingStorage storage) {
        this.storage = storage;
    }

    /**
     * Метод получения всего списка рейтингов из хранилища
     *
     * @return список всех рейтингов
     */
    public List<Rating> findAllRatings() {
        return storage.findAllRatings();
    }

    /**
     * Метод получения рейтинга по идентификатору из хранилища сервиса
     *
     * @param id идентификатор
     * @return копию объекта rating с указанным идентификатором
     */
    public Rating findRatingById(Long id) {
        Rating rating = storage.findRatingById(id);
        if (rating == null) {
            throw new NotFoundException("Рейтинг с идентификтором " + id + " не найден");
        }
        return rating;
    }
}
