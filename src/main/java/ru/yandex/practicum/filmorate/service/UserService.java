package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.HashSet;
import java.util.List;

/**
 * Класс сервиса для работы с хранилищем пользователей
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@Service
public class UserService {
    /**
     * Поле хранилище пользователей
     */
    private final UserStorage storage;
    /**
     * Поле валидатор
     */
    private final UserValidator validator;

    /**
     * Конструктор - создание нового объекта с определенными значениями
     * @param storage - хранилище пользователей
     * @param validator - валидатор пользователей
     */
    public UserService(@Qualifier("userDbStorage") UserStorage storage, UserValidator validator) {
        this.storage = storage;
        this.validator = validator;
    }

    /**
     * Метод получения всего списка пользователей из хранилища сервиса
     *
     * @return список всех пользователей
     */
    public List<User> findAllUsers() {
        List<User> users = storage.findAllUsers();
        users.forEach(user -> user.setFriends(storage.getIdFriendsByUser(user)));
        return users;
    }

    /**
     * Метод добавления пользователя в хранилище сервиса
     *
     * @param user {@link User}
     * @return копию объекта user с добавленным id
     * @throws ValidationException если объект не прошел валидацию
     */
    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(0L);
        }
        if (user.getId() != null && user.getId() > 0) {
            log.error("Попытка добавить пользователя со своим идентификатором " +
                    "(при создании генерируется автоматически)");
            throw new ValidationException("Пользователь не должен иметь идентификатора " +
                    "(при создании генерируется автоматически)");
        }

        validator.validate(user);

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user = storage.createUser(user);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        log.info("Добавлен пользователь: " + user);
        return user;
    }

    /**
     * Метод обновления пользователя в хранилище сервиса
     *
     * @param user {@link User}
     * @return копию объекта user с обновленными полями
     */
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("У пользователя не хватает идентификатора для обновления");
        }
        Long checkId = user.getId();
        checkUserId(checkId);
        validator.validate(user);
        user = storage.updateUser(user);
        user.setFriends(storage.getIdFriendsByUser(user));
        log.info("Обновлен пользователь: " + user);
        return user;
    }

    /**
     * Метод очищения списка всех пользователей в хранилище сервиса
     */
    public void deleteAllUsers() {
        storage.deleteAllUsers();
    }

    /**
     * Метод получения пользователя по идентификатору из хранилища сервиса
     *
     * @param id идентификатор
     * @return копию объекта user с указанным идентификатором
     */
    public User getUserById(Long id) {
        User user = storage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с идентификтором " + id + " не найден");
        }
        user.setFriends(storage.getIdFriendsByUser(user));
        return user;
    }

    /**
     * Метод добавления пользователей в список друзей друг друга
     *
     * @param idUser,idFriend идентификатор пользователя, который отправляет запрос на добавление,
     *                        идентификатор пользователя, которого добавляют в друзья
     */
    public void addToFriends(Long idUser, Long idFriend) {
        checkUserId(idUser);
        checkUserId(idFriend);
        // Eсли пользователь уже отправил запрос и он не был принят
        if (storage.checkFriendship(idUser, idFriend, false)) {
            log.warn("Пользователь " + idUser + " уже отправлял запрос в друзья пользователю " + idFriend);
            return;
        }
        // Eсли пользователь еще не отправлял запрос и ему не был отправлен встречный запрос
        if (!storage.checkFriendship(idUser, idFriend, false)
        && !storage.checkFriendship(idFriend, idUser, false)) {
            storage.addToFriends(idUser, idFriend);
            log.info("Пользователь " + idUser
                    + " добавлен в друзья пользователю" + idFriend);
        }
        // Eсли пользователь отправляет взаимный запрос и попадает в друзья
        if (storage.checkFriendship(idFriend, idUser, false)) {
            storage.acceptToFriends(idUser, idFriend);
            log.info("Пользователь " + idUser
                    + " одобрил заявку в друзья пользователю" + idFriend);
        }
    }

    /**
     * Метод удаления пользователей из списка друзей друг друга
     *
     * @param idUser,idFriend идентификатор пользователя, который отправляет запрос на удаление,
     *                        идентификатор пользователя, которого удаляют из друзей
     */
    public void deleteFromFriends(Long idUser, Long idFriend) {
        checkUserId(idUser);
        checkUserId(idFriend);
        // Eсли пользователь не отправлял запрос
        if (!storage.checkFriendship(idUser, idFriend, false)) {
            log.warn("Пользователь " + idUser + " не отправлял запрос в друзья пользователю " + idFriend);
            return;
        }
        // Eсли пользователь отправлял запрос и его не одобрили
        if (storage.checkFriendship(idUser, idFriend, false)) {
            storage.deleteFromFriends(idUser, idFriend);
            log.info("Пользователь " + idUser
                    + " удалился из друзей у " + idFriend);
        }
        // Eсли пользователь был в друзьях
        if (storage.checkFriendship(idUser, idFriend, true)
        || storage.checkFriendship(idFriend, idUser, true)) {
            storage.deleteFromConfirmFriends(idUser, idFriend);
            log.info("Пользователь " + idUser
                    + " удалился из друзей у " + idFriend);
        }
    }

    /**
     * Метод получения списка друзей пользователя по идентификатору из хранилища сервиса
     *
     * @param idUser идентификатор пользователя, чей список друзей запрашивается
     * @return список друзей пользователя
     */
    public List<User> getFriendsByUser(Long idUser) {
        checkUserId(idUser);
        List<User> friends = storage.getFriendsByUser(idUser);
        friends.forEach(user -> user.setFriends(storage.getIdFriendsByUser(user)));
        return friends;
    }

    /**
     * Метод получения списка общих друзей двух пользователей из хранилища сервиса
     *
     * @param idUser,otherId идентификатор пользователя, который запрашивает список общих друзей,
     *                       идентификатор пользователя, с которым идет поиск общих друзей
     * @return список общих друзей двух пользователей
     */
    public List<User> getCommonFriends(Long idUser, Long otherId) {
        checkUserId(idUser);
        checkUserId(otherId);
        List<User> commonFriends = storage.getCommonFriends(idUser, otherId);
        commonFriends.forEach(user -> user.setFriends(storage.getIdFriendsByUser(user)));
        return commonFriends;
    }

    /**
     * Метод проверки наличия в хранилище пользователей пользователя по идентификатору
     *
     * @param id идентификатор
     */
    private void checkUserId(Long id) {
        User user = storage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
    }
}
