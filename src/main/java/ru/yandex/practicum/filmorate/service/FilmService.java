package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;
    private final FilmValidator validator;

    public List<Film> findAllFilms() {
        return inMemoryFilmStorage.findAllFilms();
    }

    public Film createFilm(Film film) {
        if (film.getId() == null) {
            film.setId(0L);
        }
        if (film.getId() > 0) {
            log.error("Попытка добавить фильм со своим идентификатором " +
                    "(при создании генерируется автоматически)");
            throw new ValidationException("Фильм не должен иметь идентификатора " +
                    "(при создании генерируется автоматически)");
        }
        validator.validate(film);
        return inMemoryFilmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validator.validate(film);
        return inMemoryFilmStorage.updateFilm(film);
    }

    public void deleteAllFilms() {
        inMemoryFilmStorage.deleteAllFilms();
    }

    public Film getFilmById(Long id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    public void addLikeToFilm(Long idFilm, Long idUser) {
        inMemoryUserStorage.getUserById(idUser);
        inMemoryFilmStorage.addLikeToFilm(idFilm, idUser);
    }

    public void deleteLikeFromFilm(Long idFilm, Long idUser) {
        inMemoryUserStorage.getUserById(idUser);
        inMemoryFilmStorage.deleteLikeFromFilm(idFilm, idUser);
    }

    public List<Film> getPopularFilm(Integer count) {
        return inMemoryFilmStorage.getPopularFilm(count);
    }
}
