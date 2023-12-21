package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private Film firstFilm;
    private Film secondFilm;
    private Film thirdFilm;

    @BeforeEach
    public void beforeEach() {
        firstUser = new User("1@mail.ru", "first", "firstPerson",
                LocalDate.of(1994, 1, 18));

        secondUser = new User("2@mail.ru", "second", "secondPerson",
                LocalDate.of(1994, 1, 19));

        thirdUser = new User("3@mail.ru", "third", "thirdPerson",
                LocalDate.of(1994, 1, 20));

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        mpa.setDescription("У фильма нет возрастных ограничений");
        firstFilm = new Film("first film", "first description",
                LocalDate.of(1994, 1, 18), 100, mpa);
        firstFilm.setGenres(new HashSet<>(Arrays.asList(new Genre(1L, "Комедия"),
                new Genre(2L, "Драма"))));

        Mpa mpa1 = new Mpa();
        mpa1.setId(3L);
        mpa1.setName("PG-13");
        mpa1.setDescription("Детям до 13 лет просмотр нежелателен");
        secondFilm = new Film("second film", "second description",
                LocalDate.of(1994, 1, 19), 140, mpa1);
        secondFilm.setGenres(new HashSet<>(List.of(new Genre(6L, "Боевик"))));

        Mpa mpa2 = new Mpa();
        mpa2.setId(4L);
        mpa2.setName("R");
        mpa2.setDescription("Лицам до 17 лет просматривать фильм можно только в присутствии взрослого");
        thirdFilm = new Film("third film", "third description",
                LocalDate.of(1981, 1, 19), 148, mpa2);
        thirdFilm.setGenres(new HashSet<>(List.of(new Genre(2L, "Драма"))));
    }

    @Test
    public void testFindAllUsers() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        List<User> listUsers = userStorage.findAllUsers();
        assertThat(listUsers).contains(firstUser);
        assertThat(listUsers).contains(secondUser);
    }

    @Test
    public void testCreateUserAndGetUserById() {
        firstUser = userStorage.createUser(firstUser);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(firstUser.getId()));
        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", firstUser.getId())
                                .hasFieldOrPropertyWithValue("login", "first")
                                .hasFieldOrPropertyWithValue("email", "1@mail.ru")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(1994, 1, 18))
                                .hasFieldOrPropertyWithValue("name", "firstPerson"));

    }

    @Test
    public void testUpdateUser() {
        firstUser = userStorage.createUser(firstUser);
        User updateUser = new User("1update@mail.ru", "firstUpdate", "firstPersonUpdate",
                LocalDate.of(1994, 1, 20));
        updateUser.setId(1L);

        Optional<User> optionalUpdateUser = Optional.ofNullable(userStorage.updateUser(updateUser));
        assertThat(optionalUpdateUser)
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("name", "firstPersonUpdate")
                        .hasFieldOrPropertyWithValue("login", "firstUpdate")
                        .hasFieldOrPropertyWithValue("email", "1update@mail.ru")
                        .hasFieldOrPropertyWithValue("birthday",
                                LocalDate.of(1994, 1, 20))
                );
    }

    @Test
    public void deleteAllUsers() {
        firstUser = userStorage.createUser(firstUser);
        userStorage.deleteAllUsers();
        List<User> listUsers = userStorage.findAllUsers();
        assertThat(listUsers).hasSize(0);
    }

    @Test
    public void testAddToFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        userService.addToFriends(firstUser.getId(), secondUser.getId());
        assertThat(userService.getFriendsByUser(firstUser.getId())).hasSize(1);
        assertThat(userService.getFriendsByUser(firstUser.getId())).contains(secondUser);
        userStorage.deleteFromFriends(firstUser.getId(), secondUser.getId());
    }

    @Test
    public void testDeleteFromFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addToFriends(firstUser.getId(), secondUser.getId());
        userService.addToFriends(firstUser.getId(), thirdUser.getId());
        userService.deleteFromFriends(firstUser.getId(), secondUser.getId());
        assertThat(userService.getFriendsByUser(firstUser.getId())).hasSize(1);
        assertThat(userService.getFriendsByUser(firstUser.getId())).contains(thirdUser);
        userStorage.deleteFromFriends(firstUser.getId(), thirdUser.getId());
    }

    @Test
    public void testGetFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addToFriends(firstUser.getId(), secondUser.getId());
        userService.addToFriends(firstUser.getId(), thirdUser.getId());
        userService.addToFriends(thirdUser.getId(), firstUser.getId());
        thirdUser.setFriends(userStorage.getIdFriendsByUser(thirdUser));
        assertThat(userService.getFriendsByUser(firstUser.getId())).hasSize(2);
        assertThat(userService.getFriendsByUser(firstUser.getId())).contains(secondUser, thirdUser);
        assertThat(userStorage.getFriendsByUser(secondUser.getId())).hasSize(0);
        assertThat(userStorage.getFriendsByUser(thirdUser.getId())).hasSize(1);
        assertThat(userStorage.getFriendsByUser(thirdUser.getId())).contains(firstUser);
        userStorage.deleteFromFriends(firstUser.getId(), secondUser.getId());
        userStorage.deleteFromFriends(firstUser.getId(), thirdUser.getId());
        userStorage.deleteFromFriends(thirdUser.getId(), firstUser.getId());
    }

    @Test
    public void testGetCommonFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addToFriends(firstUser.getId(), secondUser.getId());
        userService.addToFriends(firstUser.getId(), thirdUser.getId());
        userService.addToFriends(secondUser.getId(), firstUser.getId());
        userService.addToFriends(secondUser.getId(), thirdUser.getId());
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId())).hasSize(1);
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId()))
                .contains(thirdUser);
        userStorage.deleteFromFriends(firstUser.getId(), secondUser.getId());
        userStorage.deleteFromFriends(firstUser.getId(), thirdUser.getId());
        userStorage.deleteFromFriends(secondUser.getId(), firstUser.getId());
        userStorage.deleteFromFriends(secondUser.getId(), thirdUser.getId());
    }

    @Test
    public void testGetIdFriendsByUser() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addToFriends(firstUser.getId(), secondUser.getId());
        userService.addToFriends(firstUser.getId(), thirdUser.getId());
        assertThat(userStorage.getIdFriendsByUser(firstUser)).hasSize(2);
        assertThat(userStorage.getIdFriendsByUser(firstUser)).contains(secondUser.getId());
        assertThat(userStorage.getIdFriendsByUser(firstUser)).contains(thirdUser.getId());
        userStorage.deleteFromFriends(firstUser.getId(), secondUser.getId());
        userStorage.deleteFromFriends(firstUser.getId(), thirdUser.getId());
    }

    @Test
    public void testDeleteFromConfirmFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        userService.addToFriends(firstUser.getId(), secondUser.getId());
        userService.addToFriends(secondUser.getId(), firstUser.getId());
        userService.deleteFromFriends(firstUser.getId(), secondUser.getId());
        assertThat(userStorage.getFriendsByUser(secondUser.getId())).hasSize(0);
        assertThat(userStorage.getFriendsByUser(firstUser.getId())).hasSize(1);
        assertThat(userStorage.getFriendsByUser(firstUser.getId())).contains(secondUser);
        userStorage.deleteFromFriends(firstUser.getId(), secondUser.getId());
        userStorage.deleteFromFriends(firstUser.getId(), thirdUser.getId());
    }

    @Test
    public void testFindAllFilms() {
        firstFilm = filmService.createFilm(firstFilm);
        secondFilm = filmService.createFilm(secondFilm);
        thirdFilm = filmService.createFilm(thirdFilm);
        List<Film> listFilms = filmService.findAllFilms();
        assertThat(listFilms).contains(firstFilm);
        assertThat(listFilms).contains(secondFilm);
        assertThat(listFilms).contains(thirdFilm);
    }


    @Test
    public void testCreateFilmAndGetFilmById() {
        firstFilm = filmStorage.createFilm(firstFilm);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(firstFilm.getId()));
        assertThat(filmOptional)
                .hasValueSatisfying(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("id", firstFilm.getId())
                        .hasFieldOrPropertyWithValue("description", "first description")
                        .hasFieldOrPropertyWithValue("releaseDate",
                                LocalDate.of(1994, 1, 18))
                        .hasFieldOrPropertyWithValue("duration", 100L)
                        .hasFieldOrPropertyWithValue("name", "first film")
                );
    }

    @Test
    public void testUpdateFilm() {
        firstFilm = filmStorage.createFilm(firstFilm);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        Film updateFilm = new Film("update first film", "update first description",
                LocalDate.of(1994, 1, 21), 120, mpa);
        updateFilm.setId(1L);

        Optional<Film> testUpdateFilm = Optional.ofNullable(filmStorage.updateFilm(updateFilm));
        assertThat(testUpdateFilm)
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "update first film")
                                .hasFieldOrPropertyWithValue("description", "update first description")
                                .hasFieldOrPropertyWithValue("releaseDate",
                                        LocalDate.of(1994, 1, 21))
                                .hasFieldOrPropertyWithValue("duration", 120L)
                );
    }

    @Test
    public void deleteAllFilms() {
        firstFilm = filmStorage.createFilm(firstFilm);
        secondFilm = filmStorage.createFilm(secondFilm);
        filmStorage.deleteAllFilms();
        List<Film> listFilms = filmStorage.findAllFilms();
        assertThat(listFilms).hasSize(0);
    }

    @Test
    public void testAddLikeToFilm() {
        firstUser = userStorage.createUser(firstUser);
        firstFilm = filmStorage.createFilm(firstFilm);
        filmService.addLikeToFilm(firstFilm.getId(), firstUser.getId());
        firstFilm = filmService.getFilmById(firstFilm.getId());
        assertThat(firstFilm.getLikes()).hasSize(1);
        assertThat(firstFilm.getLikes()).contains(firstUser.getId());
    }

    @Test
    public void testDeleteLikeFromFilm() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        firstFilm = filmStorage.createFilm(firstFilm);
        filmService.addLikeToFilm(firstFilm.getId(), firstUser.getId());
        filmService.addLikeToFilm(firstFilm.getId(), secondUser.getId());
        filmService.deleteLikeFromFilm(firstFilm.getId(), firstUser.getId());
        firstFilm = filmService.getFilmById(firstFilm.getId());
        assertThat(firstFilm.getLikes()).hasSize(1);
        assertThat(firstFilm.getLikes()).contains(secondUser.getId());
    }

    @Test
    public void testGetPopularFilms() {

        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);

        firstFilm = filmStorage.createFilm(firstFilm);
        filmService.addLikeToFilm(firstFilm.getId(), firstUser.getId());

        secondFilm = filmStorage.createFilm(secondFilm);
        filmService.addLikeToFilm(secondFilm.getId(), firstUser.getId());
        filmService.addLikeToFilm(secondFilm.getId(), secondUser.getId());
        filmService.addLikeToFilm(secondFilm.getId(), thirdUser.getId());

        thirdFilm = filmStorage.createFilm(thirdFilm);
        filmService.addLikeToFilm(thirdFilm.getId(), firstUser.getId());
        filmService.addLikeToFilm(thirdFilm.getId(), secondUser.getId());

        List<Film> listFilms = filmService.getPopularFilm(5);

        assertThat(listFilms).hasSize(3);

        assertThat(Optional.of(listFilms.get(0)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "second film"));

        assertThat(Optional.of(listFilms.get(1)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "third film"));

        assertThat(Optional.of(listFilms.get(2)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "first film"));
    }

    @Test
    public void testUpdateGenresByFilm() {
        firstFilm = filmService.createFilm(firstFilm);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        mpa.setDescription("У фильма нет возрастных ограничений");
        Film firstUpdateFilm = new Film("first film", "first description",
                LocalDate.of(1994, 1, 18), 100, mpa);
        firstUpdateFilm.setId(firstFilm.getId());
        firstUpdateFilm.setGenres(new HashSet<>(Arrays.asList(new Genre(1L, "Комедия"),
                new Genre(2L, "Драма"),
                new Genre(3L, "Мультфильм"))));

        filmService.updateFilm(firstUpdateFilm);
        Set<Genre> listGenres = firstUpdateFilm.getGenres();
        assertThat(listGenres).contains(new Genre(1L, "Комедия"));
        assertThat(listGenres).contains(new Genre(2L, "Драма"));
        assertThat(listGenres).contains(new Genre(3L, "Мультфильм"));
    }

    @Test
    public void testFindAllGenres() {
        List<Genre> listGenres = genreStorage.findAllGenres();
        assertThat(listGenres).contains(new Genre(1L, "Комедия"));
        assertThat(listGenres).contains(new Genre(2L, "Драма"));
        assertThat(listGenres).contains(new Genre(3L, "Мультфильм"));
        assertThat(listGenres).contains(new Genre(4L, "Триллер"));
        assertThat(listGenres).contains(new Genre(5L, "Документальный"));
        assertThat(listGenres).contains(new Genre(6L, "Боевик"));
    }

    @Test
    public void testFindGenreById() {
        Genre documentaryGenre = new Genre(5L, "Документальный");
        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage
                .findGenreById(documentaryGenre.getId()));
        assertThat(genreOptional)
                .hasValueSatisfying(genre ->
                        assertThat(genre)
                                .hasFieldOrPropertyWithValue("id", documentaryGenre.getId())
                                .hasFieldOrPropertyWithValue("name", "Документальный"));
    }

    @Test
    public void testFindAllMpa() {
        List<Mpa> listMpa = mpaStorage.findAllMpa();
        Mpa mpa1 = new Mpa();
        mpa1.setId(1L);
        mpa1.setName("G");
        mpa1.setDescription("У фильма нет возрастных ограничений");
        Mpa mpa2 = new Mpa();
        mpa2.setId(2L);
        mpa2.setName("PG");
        mpa2.setDescription("Детям рекомендуется смотреть фильм с родителями");
        Mpa mpa3 = new Mpa();
        mpa3.setId(3L);
        mpa3.setName("PG-13");
        mpa3.setDescription("Детям до 13 лет просмотр нежелателен");
        Mpa mpa4 = new Mpa();
        mpa4.setId(4L);
        mpa4.setName("R");
        mpa4.setDescription("Лицам до 17 лет просматривать фильм можно только в присутствии взрослого");
        Mpa mpa5 = new Mpa();
        mpa5.setId(5L);
        mpa5.setName("NC-17");
        mpa5.setDescription("Лицам до 18 лет просмотр запрещён");

        assertThat(listMpa).contains(mpa1);
        assertThat(listMpa).contains(mpa2);
        assertThat(listMpa).contains(mpa3);
        assertThat(listMpa).contains(mpa4);
        assertThat(listMpa).contains(mpa5);
    }
}

