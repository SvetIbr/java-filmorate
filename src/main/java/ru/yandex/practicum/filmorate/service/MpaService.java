package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaStorage;

import java.util.List;

/**
 * Класс сервиса для работы с хранилищем рейтингов
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Service
public class MpaService {
    /**
     * Поле хранилище рейтингов
     */
    private final MpaStorage storage;

    /**
     * Конструктор - создание нового объекта с определенными значениями
     *
     * @param storage - хранилище рейтингов
     */
    @Autowired
    public MpaService(MpaStorage storage) {
        this.storage = storage;
    }

    /**
     * Метод получения всего списка рейтингов из хранилища
     *
     * @return список всех рейтингов
     */
    public List<Mpa> findAllMpa() {
        return storage.findAllMpa();
    }

    /**
     * Метод получения рейтинга по идентификатору из хранилища сервиса
     *
     * @param id идентификатор
     * @return копию объекта rating с указанным идентификатором
     */
    public Mpa findMpaById(Long id) {
        Mpa mpa = storage.findMpaById(id);
        if (mpa == null) {
            throw new NotFoundException("Рейтинг с идентификтором " + id + " не найден");
        }
        return mpa;
    }
}
