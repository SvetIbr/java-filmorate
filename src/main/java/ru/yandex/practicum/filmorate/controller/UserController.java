package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс контроллера для работы с пользователями
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private static int numberId = 1;

    /**
     * Метод получения всего списка пользователя из памяти контроллера через запрос
     *
     * @return список всех пользователей и код ответа API
     */
    @GetMapping
    public ResponseEntity<List<User>> findAllUsers() {
        return new ResponseEntity<>(new ArrayList<>(users.values()), HttpStatus.OK);
    }

    /**
     * Метод добавления объекта user в память контроллера через запрос
     *
     * @param user пользователь
     * @return копию объекта user с добавленным id и код ответа API
     * @throws ValidationException если объект не прошел валидацию
     * @see Class #User
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            if (user.getId() > 0) {
                log.error("Попытка добавить пользователя со своим идентификатором " +
                        "(при создании генерируется автоматически)");
                throw new ValidationException("Пользователь не должен иметь идентификатора " +
                        "(при создании генерируется автоматически)");
            }
            validateUser(user);
            if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            user.setId(numberId);
            users.put(user.getId(), user);
            numberId++;
            log.info("Добавлен пользователь: " + user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (ValidationException exp) {
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Метод обновления объекта user через запрос
     *
     * @param user пользователь
     * @return копию объекта user с обновленными полями и код ответа API
     * @throws ValidationException если объект не прошел валидацию
     */
    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try {
            validateUser(user);
            try {
                validateForUpdate(user);
            } catch (ValidationException exp) {
                return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
            }
            users.put(user.getId(), user);
            log.info("Обновлен пользователь: " + user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ValidationException exp) {
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Метод очищения списка всех пользователей в  памяти контроллера через запрос
     *
     * @return код ответа API
     */
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllUsers() {
        users.clear();
        numberId = 1;
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateUser(User user) throws ValidationException {
        if (user == null) {
            log.error("Пользователь = Null");
            throw new ValidationException("Пользователь = null");
        }
        if (user.getEmail().isBlank()
                || user.getEmail().isEmpty()
                || !user.getEmail().contains("@")) {
            log.error("Некорректный email пользователя");
            throw new ValidationException("Email пользователя не может быть пустым " +
                    "и должен содержать \"@\"");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Логин пользователя пустой или содержит пробелы");
            throw new ValidationException("Логин пользователя не может быть пустым " +
                    "и не должен содержать пробелы");
        }
        LocalDate now = LocalDate.now();
        if (user.getBirthday().isAfter(now)) {
            log.error("Дата рождения пользователя в будущем");
            throw new ValidationException("Дата рождения пользователя не может быть в будущем");
        }
    }

    private void validateForUpdate(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователя с таким идентификатором нет");
            throw new ValidationException("Идентификатор фильма не найден");
        }
    }
}
