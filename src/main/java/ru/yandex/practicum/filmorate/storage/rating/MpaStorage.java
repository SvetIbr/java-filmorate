package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

/**
 * Интерфейс хранилища рейтингов
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
public interface MpaStorage {
    /**
     * Метод получения всего списка рейтингов из хранилища
     *
     * @return список всех рейтингов
     */
    List<Mpa> findAllMpa();

    /**
     * Метод получения рейтинга по идентификатору из хранилища сервиса
     *
     * @param id идентификатор рейтинга
     * @return копию объекта rating с указанным идентификатором
     */
    Mpa findMpaById(Long id);
}
