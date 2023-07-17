package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

/**
 * Класс контроллера для работы с пользователями
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    /**
     * Метод получения всего списка пользователя из памяти контроллера через запрос
     *
     * @return список всех пользователей и код ответа API
     */
    @GetMapping
    public List<User> findAll() {
        return service.findAllUsers();
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
    public ResponseEntity<User> create(@RequestBody User user) {
        return new ResponseEntity<>(service.createUser(user), HttpStatus.CREATED);
    }

    /**
     * Метод обновления объекта user через запрос
     *
     * @param user пользователь
     * @return копию объекта user с обновленными полями и код ответа API
     * @throws ValidationException если объект не прошел валидацию
     */
    @PutMapping
    public User update(@RequestBody User user) {
        return service.updateUser(user);
    }

    /**
     * Метод очищения списка всех пользователей в  памяти контроллера через запрос
     *
     * @return код ответа API
     */
    @DeleteMapping
    public HttpStatus deleteAll() {
        service.deleteAllUsers();
        return HttpStatus.OK;
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addToFriends(@PathVariable Long id, @PathVariable Long friendId) {
        return service.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        return service.deleteFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsByUser(@PathVariable Long id) {
        return service.getFriendsByUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return service.getCommonFriends(id, otherId);
    }

}
