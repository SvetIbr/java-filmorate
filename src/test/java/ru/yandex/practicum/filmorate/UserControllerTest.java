package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final User userWithId = new User(1L, "mail@mail.ru", "login",
            "name", LocalDate.of(1994, 1, 18));
    private final User userWithoutId = new User(0, "mail@mail.ru", "login",
            "name", LocalDate.of(1994, 1, 18));

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

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(userWithId));

        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(userWithId))));
    }

    @Test
    public void createNullUser() throws Exception {
        User user = null;
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest());

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithEmptyBody() throws Exception {
        var requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

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

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(userWithId));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithEmptyName() throws Exception {
        User user = new User(0, "mail@mail.ru", "login",
                " ", LocalDate.of(1994, 1, 18));
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();

        User user1 = new User(1, "mail@mail.ru", "login",
                "login", LocalDate.of(1994, 1, 18));
        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(user1));

        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(user1))));
    }

    @Test
    public void createUserWithFailEmail() throws Exception {
        User user = new User(0, "mail.ru", "login",
                "name", LocalDate.of(1994, 1, 18));
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(user));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithEmptyEmail() throws Exception {
        User user = new User(0, "", "login",
                "name", LocalDate.of(1994, 1, 18));
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(user));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithFailLogin() throws Exception {
        User user = new User(0, "mail@mail.ru", "login log",
                "name", LocalDate.of(1994, 1, 18));
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(user));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithEmptyLogin() throws Exception {
        User user = new User(0, "mail.ru", " ",
                "name", LocalDate.of(1994, 1, 18));
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(user));

        getListUsersMustBeEmpty();
    }

    @Test
    public void createUserWithFailBirthday() throws Exception {
        User user = new User(0, "mail.ru", "login",
                "name", LocalDate.of(2024, 1, 18));
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(user));

        getListUsersMustBeEmpty();
    }

    @Test
    public void updateUserWithValidFields() throws Exception {
        createUser();

        User updateUser = new User(1, "mail1@mail.ru", "updateLogin",
                "update name", LocalDate.of(1994, 1, 19));
        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateUser));

        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(updateUser))));
    }

    @Test
    public void updateUserWithFailId() throws Exception {
        createUser();

        User updateUser = new User(2, "mail1@mail.ru", "updateLogin",
                "update name", LocalDate.of(1994, 1, 19));
        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateUser));

        getListUsersMustHaveNotUpdateUser();
    }

    @Test
    public void updateUserWithEmptyEmail() throws Exception {
        createUser();

        User updateUser = new User(1, "", "updateLogin",
                "update name", LocalDate.of(1994, 1, 19));
        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateUser));

        getListUsersMustHaveNotUpdateUser();
    }

    @Test
    public void updateUserWithFailEmail() throws Exception {
        createUser();

        User updateUser = new User(1, "mail", "updateLogin",
                "update name", LocalDate.of(1994, 1, 19));
        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateUser));

        getListUsersMustHaveNotUpdateUser();
    }

    @Test
    public void updateUserWithFailLogin() throws Exception {
        createUser();

        User updateUser = new User(1, "mail@mail.ru", "update login",
                "update name", LocalDate.of(1994, 1, 19));
        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateUser));

        getListUsersMustHaveNotUpdateUser();
    }

    @Test
    public void updateUserWithFailBirthday() throws Exception {
        createUser();

        User updateUser = new User(1, "mail@mail.ru", "updateLogin",
                "update name", LocalDate.of(2023, 12, 19));
        var requestBuilder = put("/users")
                .content(objectMapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateUser));

        getListUsersMustHaveNotUpdateUser();
    }

    private void getListUsersMustBeEmpty() throws Exception {
        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    private void getListUsersMustHaveNotUpdateUser() throws Exception {
        var checkListUsersRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(checkListUsersRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(userWithId))));
    }

    private void createUser() throws Exception {
        var requestBuilder = post("/users")
                .content(objectMapper.writeValueAsString(userWithoutId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);
    }
}
