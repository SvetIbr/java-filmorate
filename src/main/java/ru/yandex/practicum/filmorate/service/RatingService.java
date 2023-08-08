package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Service
public class RatingService {
    private final RatingStorage storage;

    @Autowired
    public RatingService(RatingStorage storage) {
        this.storage = storage;
    }

    public List<Rating> findAllRatings() {
        return storage.findAllRatings();
    }

    public Rating findRatingById(Long id) {
        Rating rating = storage.findRatingById(id);
        if (rating == null) {
            throw new NotFoundException("Рейтинг с идентификтором " + id + " не найден");
        }
        return rating;
    }
}
