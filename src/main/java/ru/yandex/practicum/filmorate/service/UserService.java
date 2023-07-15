package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage inMemoryUserStorage;
    private final UserValidator validator;

    public List<User> findAllUsers() {
        return inMemoryUserStorage.findAllUsers();
    }

    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(0L);
        }
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

    public User updateUser(User user) {
        validator.validate(user);
        return inMemoryUserStorage.updateUser(user);
    }

    public void deleteAllUsers() {
        inMemoryUserStorage.deleteAllUsers();
    }

    public User getUserById(Long id) {
        return inMemoryUserStorage.getUserById(id);
    }

    public User addToFriends(Long idUser, Long idFriend) {
        return inMemoryUserStorage.addToFriends(idUser, idFriend);
    }

    public User deleteFromFriends(Long idUser, Long idFriend) {
        return inMemoryUserStorage.deleteFromFriends(idUser, idFriend);
    }

    public List<User> getFriendsByUser(Long idUser) {
        return inMemoryUserStorage.getFriendsByUser(idUser);
    }

    public List<User> getCommonFriends(Long idUser, Long otherId) {
        return inMemoryUserStorage.getCommonFriends(idUser, otherId);
    }
}
