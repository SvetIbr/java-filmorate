package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final Film filmWithId = new Film(1L, "title", "description",
            LocalDate.of(1994, 1, 18), 180, new HashSet<>());
    private final Film filmWithoutId = new Film(0L, "title", "description",
            LocalDate.of(1994, 1, 18), 180, new HashSet<>());

    @AfterEach
    public void afterEach() throws Exception {
        mockMvc.perform(delete("/films"));
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void createFilmWithValidFields() throws Exception {
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(filmWithoutId))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(filmWithId));

        var checkListFilmsRequest = get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListFilmsRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(filmWithId))));
    }

    @Test
    public void createFilmWithId() throws Exception {
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(filmWithId))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Фильм не должен иметь идентификатора " +
                        "(при создании генерируется автоматически)")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithBlankName() throws Exception {
        Film film = new Film(0L, " ", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Название фильма не может быть пустым")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithEmptyName() throws Exception {
        Film film = new Film(0L, "", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Название фильма не может быть пустым")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithFailDescription() throws Exception {
        Film film = new Film(0L, "name", "description description " +
                "description description description description description description " +
                "description description description description description description " +
                "description description description description description description " +
                "description description description", LocalDate.of(1994, 1, 18),
                180, new HashSet<>());
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Длина описания фильма не может быть более 200 символов")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithEmptyDescription() throws Exception {
        Film film = new Film(0L, "name", "", LocalDate.of(1994, 1, 18),
                180, new HashSet<>());
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Добавьте описание фильма")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithFailReleaseDate() throws Exception {
        Film film = new Film(0L, "name", "description",
                LocalDate.of(1893, 1, 18), 180, new HashSet<>());
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Дата релиза не может быть раньше 28 декабря 1895 года")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithFailDuration() throws Exception {
        Film film = new Film(0L, "name", "description",
                LocalDate.of(1994, 1, 18), -180, new HashSet<>());
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Продолжительность фильма не может быть меньше или равна 0")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithEmptyDuration() throws Exception {
        Film film = new Film(0L, "name", "description",
                LocalDate.of(1994, 1, 18), 0, new HashSet<>());
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Продолжительность фильма не может быть меньше или равна 0")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithEmptyBody() throws Exception {
        var requestBuilder = post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isInternalServerError())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Произошла непредвиденная ошибка.")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createNullFilm() throws Exception {
        Film film = null;
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isInternalServerError())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Произошла непредвиденная ошибка.")));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void updateFilmWithValidFields() throws Exception {
        createFilm();

        Film updateFilm = new Film(1L, "updateTitle", "update description",
                LocalDate.of(1994, 1, 19), 189, new HashSet<>());
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateFilm));

        var checkListFilmsRequest = get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListFilmsRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(updateFilm))));
    }

    @Test
    public void updateFilmWithFailId() throws Exception {
        createFilm();

        Film updateFilm = new Film(2L, "updateTitle", "update description",
                LocalDate.of(1994, 1, 19), 189, new HashSet<>());
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Идентификатор фильма не найден")));

        getListFilmsMustHaveNotUpdateFilm();
    }

    @Test
    public void updateFilmWithFailName() throws Exception {
        createFilm();

        Film updateFilm = new Film(1L, "", "update description",
                LocalDate.of(1994, 1, 19), 189, new HashSet<>());
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Название фильма не может быть пустым")));

        getListFilmsMustHaveNotUpdateFilm();
    }

    @Test
    public void updateFilmWithFailDescriptions() throws Exception {
        createFilm();

        Film updateFilm = new Film(1L, "update title", "",
                LocalDate.of(1994, 1, 19), 189, new HashSet<>());
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Добавьте описание фильма")));

        getListFilmsMustHaveNotUpdateFilm();
    }

    @Test
    public void updateFilmWithFailReleaseDate() throws Exception {
        createFilm();

        Film updateFilm = new Film(1L, "update title", "update description",
                LocalDate.of(1888, 1, 19), 189, new HashSet<>());
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Дата релиза не может быть раньше 28 декабря 1895 года")));

        getListFilmsMustHaveNotUpdateFilm();
    }

    @Test
    public void updateFilmWithFailDuration() throws Exception {
        createFilm();

        Film updateFilm = new Film(1L, "update title", "update description",
                LocalDate.of(1888, 1, 19), -189, new HashSet<>());
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Продолжительность фильма не может быть меньше или равна 0")));

        getListFilmsMustHaveNotUpdateFilm();
    }

    @Test
    public void getFilmWithValidId() throws Exception {
        createFilm();

        var requestBuilder = get("/films/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(filmWithId));
    }

    @Test
    public void getFilmWithFailId() throws Exception {
        createFilm();

        var requestBuilder = get("/films/4")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(new ErrorResponse
                        ("Идентификатор фильма не найден")));

        var checkListFilmsRequest = get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListFilmsRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(filmWithId))));
    }

    @Test
    public void putAndDeleteLikeToFilmWithValidId() throws Exception {
        createFilm();

        User user = new User(0L, "mail@mail.ru", "login",
                "name", LocalDate.of(1994, 1, 18), new HashSet<>());

        var requestBuilderForCreateUser = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateUser);

        var requestBuilderForAddLike = put("/films/1/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForAddLike)
                .andExpect(status().isOk());

        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        Film filmWithLike = new Film(1L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, likes);

        var checkListFilmsRequest = get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListFilmsRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(filmWithLike))));

        var requestBuilderForDelete = delete("/films/1/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForDelete)
                .andExpect(status().isOk());

        var checkListFilmsRequest1 = get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListFilmsRequest1)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(filmWithId))));
    }

    @Test
    public void putLikeToFilmWithFailIdFilm() throws Exception {
        createFilm();

        User user = new User(0L, "mail@mail.ru", "login",
                "name", LocalDate.of(1994, 1, 18), new HashSet<>());

        var requestBuilderForCreateUser = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateUser);

        var requestBuilderForAddLike = put("/films/2/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilderForAddLike)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Идентификатор фильма не найден")));

    }

    @Test
    public void putLikeToFilmWithFailIdUser() throws Exception {
        createFilm();

        User user = new User(0L, "mail@mail.ru", "login",
                "name", LocalDate.of(1994, 1, 18), new HashSet<>());

        var requestBuilderForCreateUser = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateUser);

        var requestBuilderForAddLike = put("/films/1/like/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilderForAddLike)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Идентификатор пользователя не найден")));

    }

    @Test
    public void deleteLikeToFilmWithFailIdFilm() throws Exception {
        createFilm();

        User user = new User(0L, "mail@mail.ru", "login",
                "name", LocalDate.of(1994, 1, 18), new HashSet<>());

        var requestBuilderForCreateUser = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateUser);

        var requestBuilderForAddLike = put("/films/1/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForAddLike);

        var requestBuilderForDelete = delete("/films/2/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilderForDelete)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Идентификатор фильма не найден")));

        var checkListFilmsRequest1 = get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        Film filmWithLike = new Film(1L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, likes);

        mockMvc.perform(checkListFilmsRequest1)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(filmWithLike))));
    }

    @Test
    public void deleteLikeToFilmWithFailIdUser() throws Exception {
        createFilm();

        User user = new User(0L, "mail@mail.ru", "login",
                "name", LocalDate.of(1994, 1, 18), new HashSet<>());

        var requestBuilderForCreateUser = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateUser);

        var requestBuilderForAddLike = put("/films/1/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForAddLike);

        var requestBuilderForDelete = delete("/films/1/like/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilderForDelete)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Идентификатор пользователя не найден")));

        var checkListFilmsRequest1 = get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        Film filmWithLike = new Film(1L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, likes);

        mockMvc.perform(checkListFilmsRequest1)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(filmWithLike))));
    }

    @Test
    public void getFilmsByPopularity() throws Exception {
        for (int i = 0; i < 3; i++) {
            createFilm();
        }

        User user = new User(0L, "mail@mail.ru", "login",
                "name", LocalDate.of(1994, 1, 18), new HashSet<>());

        var requestBuilderForCreateUser = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateUser);

        User user1 = new User(0L, "mail@mail.ru", "login",
                "name", LocalDate.of(1994, 1, 18), new HashSet<>());

        var requestBuilderForCreateUser1 = post("/users")
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateUser1);

        User user2 = new User(0L, "mail@mail.ru", "login",
                "name", LocalDate.of(1994, 1, 18), new HashSet<>());

        var requestBuilderForCreateUser2 = post("/users")
                .content(objectMapper.writeValueAsString(user2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateUser2);

        var requestBuilderForAddLike = put("/films/2/like/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForAddLike);

        var requestBuilderForAddLike1 = put("/films/2/like/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForAddLike1);

        var requestBuilderForAddLike2 = put("/films/3/like/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForAddLike2);

        var requestBuilderForGetPopularFilms = get("/films/popular")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        likes.add(2L);
        Film filmWithTwoLike = new Film(2L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, likes);

        Set<Long> likes1 = new HashSet<>();
        likes1.add(3L);
        Film filmWithOneLike = new Film(3L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, likes1);

        mockMvc.perform(requestBuilderForGetPopularFilms)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString
                        (List.of(filmWithTwoLike, filmWithOneLike, filmWithId))));

    }

    @Test
    public void getPopularFilmsWithoutCount() throws Exception {
        for (int i = 0; i < 13; i++) {
            createFilm();
        }

        Film film1 = new Film(2L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        Film film2 = new Film(3L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        Film film3 = new Film(4L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        Film film4 = new Film(5L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        Film film5 = new Film(6L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        Film film6 = new Film(7L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        Film film7 = new Film(8L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        Film film8 = new Film(9L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());
        Film film9 = new Film(10L, "title", "description",
                LocalDate.of(1994, 1, 18), 180, new HashSet<>());


        var requestBuilderForGetPopularFilms = get("/films/popular")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForGetPopularFilms)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString
                        (List.of(filmWithId, film1, film2, film3, film4, film5, film6, film7, film8, film9))));

    }

    @Test
    public void getPopularFilmsWithCount() throws Exception {
        for (int i = 0; i < 3; i++) {
            createFilm();
        }

        var requestBuilderForGetPopularFilms = get("/films/popular?count=1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForGetPopularFilms)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString
                        (List.of(filmWithId))));
    }

    private void getListFilmsMustBeEmpty() throws Exception {
        var checkListFilmsRequest = get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListFilmsRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    private void getListFilmsMustHaveNotUpdateFilm() throws Exception {
        var checkListFilmsRequest = get("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListFilmsRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(filmWithId))));
    }

    private void createFilm() throws Exception {
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(filmWithoutId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);
    }
}