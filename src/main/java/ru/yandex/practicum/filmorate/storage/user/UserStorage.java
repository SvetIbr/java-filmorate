package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс хранилища пользователей
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
public interface UserStorage {
    /**
     * Метод получения всего списка пользователей из хранилища
     *
     * @return список всех пользователей
     */
    List<User> findAllUsers();

    /**
     * Метод добавления пользователя в хранилище
     *
     * @param user {@link User}
     * @return копию объекта user с добавленным id
     */
    User createUser(User user);

    /**
     * Метод обновления пользователя в хранилище
     *
     * @param user {@link User}
     * @return копию объекта user с обновленными полями
     */
    User updateUser(User user);

    /**
     * Метод очищения списка всех пользователей в хранилище
     */
    void deleteAllUsers();

    /**
     * Метод получения пользователя по идентификатору из хранилища
     *
     * @param id идентификатор
     * @return копию объекта user с указанным идентификатором
     */
    User getUserById(Long id);

    /**
     * Метод добавления пользователей из хранилища в список друзей друг друга
     *
     * @param idUser,idFriend идентификатор пользователя, который отправляет запрос на добавление,
     *                        идентификатор пользователя, которого добавляют в друзья
     * @return копию объекта user, которого добавили в друзья
     */
    User addToFriends(Long idUser, Long idFriend);

    /**
     * Метод удаления пользователей из хранилища из списка друзей друг друга
     *
     * @param idUser,idFriend идентификатор пользователя, который отправляет запрос на удаление,
     *                        идентификатор пользователя, которого удаляют из друзей
     * @return копию объекта user, которого удалили из друзей
     */
    User deleteFromFriends(Long idUser, Long idFriend);

    /**
     * Метод получения списка друзей пользователя по идентификатору из хранилища
     *
     * @param idUser идентификатор пользователя, чей список друзей запрашивается
     * @return список друзей пользователя
     */
    List<User> getFriendsByUser(Long idUser);

    /**
     * Метод получения списка общих друзей двух пользователей из хранилища
     *
     * @param idUser,otherId идентификатор пользователя, который запрашивает список общих друзей,
     *                       идентификатор пользователя, с которым идет поиск общих друзей
     * @return список общих друзей двух пользователей
     */
    List<User> getCommonFriends(Long idUser, Long otherId);
}
