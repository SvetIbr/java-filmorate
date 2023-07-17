package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static Long numberId = 1L;

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        user.setId(numberId);
        user.setFriends(new HashSet<>());
        numberId++;
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: " + user);
        return user;
    }

    public User updateUser(User user) {
        checkId(user.getId());
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: " + user);
        return user;
    }
    public void deleteAllUsers() {
        users.clear();
        numberId = 1L;
    }

    public User addToFriends (Long idUser, Long idFriend) {
        checkId(idUser);
        checkId(idFriend);
        users.get(idUser).getFriends().add(idFriend);
        users.get(idFriend).getFriends().add(idUser);
        log.info("Пользователь " + users.get(idUser).getName()
                + " добавил в друзья " + users.get(idFriend).getName());
        return users.get(idFriend);
    }

    public User deleteFromFriends(Long idUser, Long idFriend) {
        checkId(idUser);
        checkId(idFriend);
        users.get(idUser).getFriends().remove(idFriend);
        users.get(idFriend).getFriends().remove(idUser);
        log.info("Пользователь " + users.get(idUser).getName()
                + " удалил из друзей " + users.get(idFriend).getName());
        return users.get(idFriend);
    }

    public List<User> getFriendsByUser(Long idUser) {
        checkId(idUser);
        List<User> friends = new ArrayList<>();
        if (!users.get(idUser).getFriends().isEmpty()) {
            for (Long cur : users.get(idUser).getFriends()) {
                friends.add(users.get(cur));
            }
        }
        return friends;
    }

    public List<User> getCommonFriends(Long idUser, Long otherId) {
        checkId(idUser);
        checkId(otherId);
        List<User> commonFriends = new ArrayList<>();
        if (!users.get(idUser).getFriends().isEmpty() || !users.get(otherId).getFriends().isEmpty()) {
            for (Long cur : users.get(idUser).getFriends()) {
                if (users.get(otherId).getFriends().contains(cur)) {
                    commonFriends.add(users.get(cur));
                }
            }
        }
        return commonFriends;
    }

    public User getUserById(Long id) {
        checkId(id);
        return users.get(id);
    }

    private void checkId(Long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователя с таким идентификатором нет");
            throw new NotFoundException("Идентификатор пользователя не найден");
        }
    }
}


