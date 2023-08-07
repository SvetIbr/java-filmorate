package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.List;

/**
 * Класс сервиса для работы с хранилищем пользователей
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    /**
     * Поле хранилище пользователей
     */
    private final UserStorage inMemoryUserStorage;
    /**
     * Поле валидатор
     */
    private final UserValidator validator;

    /**
     * Метод получения всего списка пользователей из хранилища сервиса
     *
     * @return список всех пользователей
     */
    public List<User> findAllUsers() {
        return inMemoryUserStorage.findAllUsers();
    }

    /**
     * Метод добавления пользователя в хранилище сервиса
     *
     * @param user {@link User}
     * @return копию объекта user с добавленным id
     * @throws ValidationException если объект не прошел валидацию
     */
    public User createUser(User user) {
//        if (user.getId() == null) {
//            user.setId(0L);
//        }
        if (user.getId() > 0) {
            log.error("Попытка добавить пользователя со своим идентификатором " +
                    "(при создании генерируется автоматически)");
            throw new ValidationException("Пользователь не должен иметь идентификатора " +
                    "(при создании генерируется автоматически)");
        }

        validator.validate(user);

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return inMemoryUserStorage.createUser(user);
    }

    /**
     * Метод обновления пользователя в хранилище сервиса
     *
     * @param user {@link User}
     * @return копию объекта user с обновленными полями
     */
    public User updateUser(User user) {
        validator.validate(user);
        return inMemoryUserStorage.updateUser(user);
    }

    /**
     * Метод очищения списка всех пользователей в хранилище сервиса
     */
    public void deleteAllUsers() {
        inMemoryUserStorage.deleteAllUsers();
    }

    /**
     * Метод получения пользователя по идентификатору из хранилища сервиса
     *
     * @param id идентификатор
     * @return копию объекта user с указанным идентификатором
     */
    public User getUserById(Long id) {
        return inMemoryUserStorage.getUserById(id);
    }

    /**
     * Метод добавления пользователей в список друзей друг друга
     *
     * @param idUser,idFriend идентификатор пользователя, который отправляет запрос на добавление,
     *                        идентификатор пользователя, которого добавляют в друзья
     * @return копию объекта user, которого добавили в друзья
     */
    public User addToFriends(Long idUser, Long idFriend) {
        return inMemoryUserStorage.addToFriends(idUser, idFriend);
    }

    /**
     * Метод удаления пользователей из списка друзей друг друга
     *
     * @param idUser,idFriend идентификатор пользователя, который отправляет запрос на удаление,
     *                        идентификатор пользователя, которого удаляют из друзей
     * @return копию объекта user, которого удалили из друзей
     */
    public User deleteFromFriends(Long idUser, Long idFriend) {
        return inMemoryUserStorage.deleteFromFriends(idUser, idFriend);
    }

    /**
     * Метод получения списка друзей пользователя по идентификатору из хранилища сервиса
     *
     * @param idUser идентификатор пользователя, чей список друзей запрашивается
     * @return список друзей пользователя
     */
    public List<User> getFriendsByUser(Long idUser) {
        return inMemoryUserStorage.getFriendsByUser(idUser);
    }

    /**
     * Метод получения списка общих друзей двух пользователей из хранилища сервиса
     *
     * @param idUser,otherId идентификатор пользователя, который запрашивает список общих друзей,
     *                       идентификатор пользователя, с которым идет поиск общих друзей
     * @return список общих друзей двух пользователей
     */
    public List<User> getCommonFriends(Long idUser, Long otherId) {
        return inMemoryUserStorage.getCommonFriends(idUser, otherId);
    }
}
