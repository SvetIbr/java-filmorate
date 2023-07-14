package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();
    User createUser(User user);
    User updateUser(User user);
    void deleteAllUsers();
    User addToFriends (Long idUser, Long idFriend);
    User deleteFromFriends(Long idUser, Long idFriend);
    List<User> getFriendsByUser(Long idUser);
    List<User> getCommonFriends(Long idUser, Long otherId);
    public User getUserById(Long id);
}
