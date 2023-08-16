package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final User userWithId = new User("mail@mail.ru", "login", "name",
            LocalDate.of(1994, 1, 18));
    private final User userWithoutId = new User("mail@mail.ru", "login", "name",
            LocalDate.of(1994, 1, 18));

    @BeforeEach
    public void beforeEach() {
        userWithId.setId(1L);
    }

    @AfterEach
    public void afterEach() throws Exception {
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void createUserWithValidFields() throws Exception {
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(userWithoutId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(userWithId));

        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(Collections.singletonList(userWithId))));
    }

    @Test
    public void createNullUser() throws Exception {
        User user = null;
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isInternalServerError())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Произошла непредвиденная ошибка.")));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithEmptyBody() throws Exception {
        var requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isInternalServerError())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Произошла непредвиденная ошибка.")));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithId() throws Exception {
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(userWithId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Пользователь не должен иметь идентификатора " +
                        "(при создании генерируется автоматически)")));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithEmptyName() throws Exception {
        User user = new User("mail@mail.ru", "login", " ",
                LocalDate.of(1994, 1, 18));

        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();

        User user1 = new User("mail@mail.ru", "login", "login",
                LocalDate.of(1994, 1, 18));
        user1.setId(1L);

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(user1));

        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(Collections.singletonList(user1))));
    }

    @Test
    public void createUserWithFailEmail() throws Exception {
        User user = new User("mail.ru", "login", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Email пользователя " +
                        "не может быть пустым и должен содержать \"@\"")));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithEmptyEmail() throws Exception {
        User user = new User("", "login", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Email пользователя " +
                        "не может быть пустым и должен содержать \"@\"")));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithFailLogin() throws Exception {
        User user = new User("mail@mail.ru", "login log", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Логин пользователя " +
                        "не может быть пустым и не должен содержать пробелы")));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithEmptyLogin() throws Exception {
        User user = new User("mail@mail.ru", " ", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Логин пользователя " +
                        "не может быть пустым и не должен содержать пробелы")));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithFailBirthday() throws Exception {
        User user = new User("mail@mail.ru", "login", "name",
                LocalDate.of(2024, 1, 18));

        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Дата рождения пользователя " +
                        "не может быть в будущем")));

        getListUsersMustBeEmpty();
    }

    @Test
    public void updateUserWithValidFields() throws Exception {
        createUser();

        User updateUser = new User("mail1@mail.ru", "updateLogin", "update name",
                LocalDate.of(1994, 1, 19));
        updateUser.setId(1L);

        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateUser));

        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(Collections.singletonList(updateUser))));
    }

    @Test
    public void updateUserWithFailId() throws Exception {
        createUser();

        User updateUser = new User("mail1@mail.ru", "updateLogin", "update name",
                LocalDate.of(1994, 1, 19));
        updateUser.setId(2L);

        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Пользователь с идентификатором 2 не найден")));

        getListUsersMustHaveNotUpdateUser();
    }

    @Test
    public void updateUserWithEmptyEmail() throws Exception {
        createUser();

        User updateUser = new User("", "updateLogin", "update name",
                LocalDate.of(1994, 1, 19));
        updateUser.setId(1L);

        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Email пользователя " +
                        "не может быть пустым и должен содержать \"@\"")));

        getListUsersMustHaveNotUpdateUser();
    }

    @Test
    public void updateUserWithFailEmail() throws Exception {
        createUser();

        User updateUser = new User("mail", "updateLogin", "update name",
                LocalDate.of(1994, 1, 19));
        updateUser.setId(1L);

        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Email пользователя " +
                        "не может быть пустым и должен содержать \"@\"")));

        getListUsersMustHaveNotUpdateUser();
    }

    @Test
    public void updateUserWithFailLogin() throws Exception {
        createUser();

        User updateUser = new User("mail@mail.ru", "update login", "update name",
                LocalDate.of(1994, 1, 19));
        updateUser.setId(1L);

        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Логин пользователя " +
                        "не может быть пустым и не должен содержать пробелы")));

        getListUsersMustHaveNotUpdateUser();
    }

    @Test
    public void updateUserWithFailBirthday() throws Exception {
        createUser();

        User updateUser = new User("mail@mail.ru", "updateLogin", "update name",
                LocalDate.of(2023, 12, 19));
        updateUser.setId(1L);

        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Дата рождения " +
                        "пользователя не может быть в будущем")));

        getListUsersMustHaveNotUpdateUser();
    }

    @Test
    public void getUserWithValidId() throws Exception {
        createUser();

        var requestBuilder = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(userWithId));
    }

    @Test
    public void getUserWithFailId() throws Exception {
        createUser();

        var requestBuilder = get("/users/4")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Пользователь " +
                        "с идентификатором 4 не найден")));

        var checkListFilmsRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListFilmsRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(Collections.singletonList(userWithId))));
    }

    @Test
    public void addToFriendAndGetFriendsWithValidId() throws Exception {
        createUser();

        User friend = new User("mailFriend@mail.ru", "loginFriend", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateFriend = post("/users")
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateFriend);

        var requestBuilder = put("/users/1/friends/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        var requestBuilderForCheckFriendsByUser = get("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult2 = mockMvc.perform(requestBuilderForCheckFriendsByUser)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody2 = mvcResult2
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        User userWithoutFriend = new User("mailFriend@mail.ru",
                "loginFriend", "name",
                LocalDate.of(1994, 1, 18));
        userWithoutFriend.setId(2L);

        assertThat(actualResponseBody2).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(Collections.singletonList(userWithoutFriend)));

        var requestBuilderForCheckFriendsByFriend = get("/users/2/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult3 = mockMvc.perform(requestBuilderForCheckFriendsByFriend)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody3 = mvcResult3
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody3).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(Collections.emptyList()));

    }

    @Test
    public void addToFriendWithFailId() throws Exception {
        createUser();

        User friend = new User("mailFriend@mail.ru", "loginFriend",
                "name", LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateFriend = post("/users")
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateFriend);

        var requestBuilder = put("/users/3/friends/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);


        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Пользователь " +
                        "с идентификатором 3 не найден")));
    }

    @Test
    public void addToFriendWithFailIdFriend() throws Exception {
        createUser();

        User friend = new User("mailFriend@mail.ru", "loginFriend", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateFriend = post("/users")
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateFriend);

        var requestBuilder = put("/users/1/friends/7")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);


        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Пользователь " +
                        "с идентификатором 7 не найден")));
    }

    @Test
    public void deleteFromFriendAndGetFriendsWithValidId() throws Exception {
        createUser();

        User friend = new User("mailFriend@mail.ru", "loginFriend", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateFriend = post("/users")
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateFriend);

        User friend2 = new User("mailFriend2@mail.ru", "loginFriend2", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateFriend2 = post("/users")
                .content(objectMapper.writeValueAsString(friend2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateFriend2);

        var requestBuilder = put("/users/1/friends/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        var requestBuilder2 = put("/users/1/friends/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder2)
                .andExpect(status().isOk());

        var requestBuilderForDelete = delete("/users/1/friends/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForDelete)
                .andExpect(status().isOk());

        var requestBuilderForCheckFriendsByUser = get("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult2 = mockMvc.perform(requestBuilderForCheckFriendsByUser)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody2 = mvcResult2
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        User friend3 = new User("mailFriend2@mail.ru", "loginFriend2", "name",
                LocalDate.of(1994, 1, 18));
        friend3.setId(3L);

        assertThat(actualResponseBody2).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(Collections.singletonList(friend3)));
    }

    @Test
    public void deleteFromFriendWithFailId() throws Exception {
        createUser();

        User friend = new User("mailFriend@mail.ru", "loginFriend", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateFriend = post("/users")
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateFriend);

        var requestBuilder = put("/users/1/friends/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());


        var requestBuilderForDelete = delete("/users/3/friends/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilderForDelete)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Пользователь " +
                        "с идентификатором 3 не найден")));

        var requestBuilderForCheckFriendsByUser = get("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult3 = mockMvc.perform(requestBuilderForCheckFriendsByUser)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody3 = mvcResult3
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        User friend1 = new User("mailFriend@mail.ru", "loginFriend", "name",
                LocalDate.of(1994, 1, 18));
        friend1.setId(2L);
        assertThat(actualResponseBody3).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(Collections.singletonList(friend1)));
    }

    @Test
    public void deleteFromFriendWithFailIdFriend() throws Exception {
        createUser();

        User friend = new User("mailFriend@mail.ru", "loginFriend", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateFriend = post("/users")
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateFriend);

        var requestBuilder = put("/users/1/friends/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        var requestBuilderForDelete = delete("/users/1/friends/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilderForDelete)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(new ErrorResponse("Пользователь " +
                        "с идентификатором 3 не найден")));

        var requestBuilderForCheckFriendsByUser = get("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult3 = mockMvc.perform(requestBuilderForCheckFriendsByUser)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody3 = mvcResult3
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        User friend1 = new User("mailFriend@mail.ru", "loginFriend", "name",
                LocalDate.of(1994, 1, 18));
        friend1.setId(2L);
        assertThat(actualResponseBody3).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(Collections.singletonList(friend1)));
    }


    @Test
    public void getEmptyCommonFriend() throws Exception {
        createUser();

        User friend = new User("mailFriend@mail.ru", "loginFriend", "name",
                LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateFriend = post("/users")
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateFriend);


        var requestBuilderForCheckCommonFriend = get("/users/1/friends/common/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilderForCheckCommonFriend)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(Collections.emptyList()));
    }

    @Test
    public void getCommonFriendWithValidId() throws Exception {
        createUser();

        User friend = new User("mailFriend@mail.ru", "loginFriend", "friend",
                LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateFriend = post("/users")
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateFriend);

        User commonFriend = new User("mailCommonFriend@mail.ru",
                "loginCommonFriend", "common",
                LocalDate.of(1994, 1, 18));

        var requestBuilderForCreateCommonFriend = post("/users")
                .content(objectMapper.writeValueAsString(commonFriend))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForCreateCommonFriend);

        var requestBuilderForAddToFriend = put("/users/1/friends/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForAddToFriend)
                .andExpect(status().isOk());

        var requestBuilderForAddToFriend1 = put("/users/1/friends/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForAddToFriend1)
                .andExpect(status().isOk());

        var requestBuilderForAddToFriend2 = put("/users/2/friends/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderForAddToFriend2)
                .andExpect(status().isOk());

        var requestBuilderForCheckCommonFriend = get("/users/1/friends/common/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilderForCheckCommonFriend)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        User commonFriend1 = new User("mailCommonFriend@mail.ru", "loginCommonFriend",
                "common", LocalDate.of(1994, 1, 18));
        commonFriend1.setId(3L);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper
                .writeValueAsString(Collections.singletonList(commonFriend1)));

    }

    private void getListUsersMustBeEmpty() throws Exception {
        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(Collections.emptyList())));
    }

    private void getListUsersMustHaveNotUpdateUser() throws Exception {
        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(Collections.singletonList(userWithId))));
    }

    private void createUser() throws Exception {
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(userWithoutId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);
    }
}
