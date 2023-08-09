package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

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
     * @param idUser   - идентификатор пользователя, который отправляет запрос на добавление,
     * @param idFriend - идентификатор пользователя, которого добавляют в друзья
     */
    void addToFriends(Long idUser, Long idFriend);

    /**
     * Метод удаления пользователей из хранилища из списка друзей друг друга
     *
     * @param idUser   - идентификатор пользователя, который отправляет запрос на удаление
     * @param idFriend - идентификатор пользователя, которого удаляют из друзей
     */
    void deleteFromFriends(Long idUser, Long idFriend);

    /**
     * Метод получения списка друзей пользователя по идентификатору из хранилища
     *
     * @param idUser - идентификатор пользователя, чей список друзей запрашивается
     * @return список друзей пользователя
     */
    List<User> getFriendsByUser(Long idUser);

    /**
     * Метод получения списка общих друзей двух пользователей из хранилища
     *
     * @param idUser  - идентификатор пользователя, который запрашивает список общих друзей
     * @param otherId - идентификатор пользователя, с которым идет поиск общих друзей
     * @return список общих друзей двух пользователей
     */
    List<User> getCommonFriends(Long idUser, Long otherId);

    /**
     * Метод получения списка идентификаторов друзей пользователя из хранилища
     *
     * @param user {@link User}
     * @return список идентификаторов друзей пользователя
     */
    Set<Long> getIdFriendsByUser(User user);

    /**
     * Метод проверки наличия пользователя в списке друзей другого пользователя
     *
     * @param userId    - идентификатор пользователя, отправившего запрос в друзья
     * @param friendId  - идентификатор пользователя, которому отправили запрос
     * @param confirmed - результат отправки запроса
     * @return true, если пользователь userId попадает в список друзей friendId по взаимному запросу
     * false, если пользователь friendId не отправлял встречный запрос в друзья
     */
    boolean checkFriendship(Long userId, Long friendId, Boolean confirmed);

    /**
     * Метод отправки встречного запроса в друзья - пользователи попадают в список друзей друг друга
     *
     * @param idUser   - идентификатор пользователя, отправившего запрос в друзья
     * @param idFriend - идентификатор пользователя, которому отправили запрос
     */
    void acceptToFriends(Long idUser, Long idFriend);

    /**
     * Метод удаления пользователя из списка друзей - idUser удаляет друга,
     * поэтому idFriend удаляется из списка друзей idUser, но idUser остается в списке друзей idFriend
     *
     * @param idUser   - идентификатор пользователя, который удаляет друга
     * @param idFriend - идентификатор пользователя, которого удаляют из друзей
     */
    void deleteFromConfirmFriends(Long idUser, Long idFriend);
}
