package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

/**
 * Класс сервиса для работы с хранилищем жанров
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Service
public class GenreService {
    /**
     * Поле хранилище жанров
     */
    private final GenreStorage storage;

    /**
     * Конструктор - создание нового объекта с определенными значениями
     *
     * @param storage - хранилище жанров
     */
    @Autowired
    public GenreService(GenreStorage storage) {
        this.storage = storage;
    }

    /**
     * Метод получения всего списка жанров из хранилища
     *
     * @return список всех жанров
     */
    public List<Genre> findAllGenres() {
        return storage.findAllGenres();
    }

    /**
     * Метод получения жанра по идентификатору из хранилища сервиса
     *
     * @param id идентификатор
     * @return копию объекта genre с указанным идентификатором
     */
    public Genre findGenreById(Long id) {
        Genre genre = storage.findGenreById(id);
        if (genre == null) {
            throw new NotFoundException("Жанр с идентификтором " + id + " не найден");
        }
        return genre;
    }
}
