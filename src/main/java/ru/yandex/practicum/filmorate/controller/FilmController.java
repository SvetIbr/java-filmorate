package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс контроллера для работы с фильмами
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private static int numberId = 1;
    private static final LocalDate START_OF_ANY_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    /**
     * Метод получения всего списка фильмов из памяти контроллера через запрос
     *
     * @return список всех фильмов и код ответа API
     */
    @GetMapping
    public ResponseEntity<List<Film>> findAllFilms() {
        return new ResponseEntity<>(new ArrayList<>(films.values()), HttpStatus.OK);
    }

    /**
     * Метод добавления объекта film в память контроллера через запрос
     *
     * @param film фильм
     * @return копию объекта фильм с добавленным id и код ответа API
     * @throws ValidationException если объект не прошел валидацию
     */
    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        try {
            if (film.getId() > 0) {
                log.error("Попытка добавить фильм со своим идентификатором " +
                        "(при создании генерируется автоматически)");
                throw new ValidationException("Фильм не должен иметь идентификатора " +
                        "(при создании генерируется автоматически)");
            }
            validateFilm(film);
            film.setId(numberId);
            films.put(film.getId(), film);
            numberId++;
            log.info("Добавлен фильм: " + film);
            return new ResponseEntity<>(film, HttpStatus.CREATED);
        } catch (ValidationException exp) {
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Метод обновления объекта film через запрос
     *
     * @param film фильм
     * @return копию объекта film с обновленными полями и код ответа API
     * @throws ValidationException если объект не прошел валидацию
     */
    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            try {
                validateForUpdate(film);
            } catch (ValidationException exp) {
                return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
            }
            films.put(film.getId(), film);
            log.info("Обновлен фильм: " + film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (ValidationException exp) {
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Метод очищения списка всех фильмов в  памяти контроллера через запрос
     *
     * @return код ответа API
     */
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllFilms() {
        films.clear();
        numberId = 1;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Фильм = null");
        }
        if (film.getName().isEmpty()
                || film.getName().isBlank()
                || film.getName() == null) {
            log.error("Название фильма пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() == null
                || film.getDescription().length() > 200
                || film.getDescription().isEmpty()) {
            log.error("Описание фильма больше 200 символов");
            throw new ValidationException("Длина описания фильма не может быть более 200 символов");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма меньше или равна 0");
            throw new ValidationException("Продолжительность фильма не может быть меньше или равна 0");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(START_OF_ANY_RELEASE_DATE)) {
            log.error("Дата релиза фильма раньше 28 декабря 1985");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }


    private void validateForUpdate(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильма с таким идентификатором нет");
            throw new ValidationException("Идентификатор фильма не найден");
        }
    }

}
