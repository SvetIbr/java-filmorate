package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

/**
 * Класс контроллера для работы с запросами к сервису пользователей
 *
 * @author Светлана Ибраева
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    /**
     * Поле сервис для работы с хранилищем пользователей
     */
    private final UserService service;

    /**
     * Метод получения всего списка пользователей из хранилища через запрос
     *
     * @return список всех пользователей
     */
    @GetMapping
    public List<User> findAll() {
        return service.findAllUsers();
    }

    /**
     * Метод добавления пользователя в хранилище сервиса через запрос
     *
     * @param user {@link User}
     * @return копию объекта user с добавленным id код ответа API 201
     */
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return new ResponseEntity<>(service.createUser(user), HttpStatus.CREATED);
    }

    /**
     * Метод обновления пользователя в хранилище сервиса через запрос
     *
     * @param user {@link User}
     * @return копию объекта user с обновленными полями
     */
    @PutMapping
    public User update(@RequestBody User user) {
        return service.updateUser(user);
    }

    /**
     * Метод очищения списка всех пользователей в хранилище сервиса через запрос
     *
     * @return код ответа API
     */
    @DeleteMapping
    public HttpStatus deleteAll() {
        service.deleteAllUsers();
        return HttpStatus.OK;
    }

    /**
     * Метод получения пользователя по идентификатору из хранилища сервиса через запрос
     *
     * @param id идентификатор
     * @return копию объекта user с указанным идентификатором
     */
    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    /**
     * Метод добавления пользователей в список друзей друг друга через запрос
     *
     * @param id,friendId идентификатор пользователя, который отправляет запрос на добавление,
     *                    идентификатор пользователя, которого добавляют в друзья
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable Long id, @PathVariable Long friendId) {
        service.addToFriends(id, friendId);
    }

    /**
     * Метод удаления пользователя из списка друзей друг друга через запрос
     *
     * @param id,friendId идентификатор пользователя, который отправляет запрос на удаление,
     *                    идентификатор пользователя, которого удаляют из друзей
     * @return копию объекта user, которого удалили из друзей
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFromFriends(id, friendId);
    }

    /**
     * Метод получения списка друзей пользователя по идентификатору из хранилища сервиса  через запрос
     *
     * @param id идентификатор пользователя, чей список друзей запрашивается
     * @return список друзей пользователя
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriendsByUser(@PathVariable Long id) {
        return service.getFriendsByUser(id);
    }

    /**
     * Метод получения списка общих друзей двух пользователей из хранилища сервиса  через запрос
     *
     * @param id,otherId идентификаторы пользователей
     * @return список общих друзей двух пользователей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return service.getCommonFriends(id, otherId);
    }

}
