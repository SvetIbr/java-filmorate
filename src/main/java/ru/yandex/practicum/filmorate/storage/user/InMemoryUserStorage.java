package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
        numberId++;
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        return user;
    }

    public void deleteAllUsers() {
        users.clear();
        numberId = 1L;
    }

    public User getUserById(Long id) {
        return users.getOrDefault(id, null);
    }

    public void addToFriends(Long idUser, Long idFriend) {
        users.get(idFriend).getFriends().add(idUser);
    }

    public void deleteFromFriends(Long idUser, Long idFriend) {
        users.get(idFriend).getFriends().remove(idUser);
    }

    public List<User> getFriendsByUser(Long idUser) {
        List<User> friends = new ArrayList<>();
        if (!users.get(idUser).getFriends().isEmpty()) {
            for (Long cur : users.get(idUser).getFriends()) {
                friends.add(users.get(cur));
            }
        }
        return friends;
    }

    public List<User> getCommonFriends(Long idUser, Long otherId) {
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

    public Set<Long> getIdFriendsByUser(User user) {
        return users.get(user.getId()).getFriends();
    }

    public boolean checkFriendship(Long userId, Long friendId, Boolean confirmed) {
        return users.get(userId).getFriends().contains(friendId) == confirmed;
    }

    public void acceptToFriends(Long idUser, Long idFriend) {
        users.get(idFriend).getFriends().add(idUser);
    }

    public void deleteFromConfirmFriends(Long idUser, Long idFriend) {
        users.get(idFriend).getFriends().remove(idUser);
    }
}



