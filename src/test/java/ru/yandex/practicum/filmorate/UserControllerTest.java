package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createUserWithValidFieldsAndUpdateWithValidFields() throws Exception {
        var requestBuilder = post("/users")
                .content("{\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1," +
                        "\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}"));

        var requestBuilder1 = put("/users")
                .content("{\"id\": 1," +
                        "\"login\": \"updateLogin\", \"name\": \"updateName\", " +
                        "\"email\": \"mail2@mail.ru\", \"birthday\": \"1946-08-21\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder1)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1," +
                                "\"login\": \"updateLogin\", \"name\": \"updateName\", " +
                                "\"email\": \"mail2@mail.ru\", \"birthday\": \"1946-08-21\"}"));
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void createUserWithId() throws Exception {
        var requestBuilder = post("/users")
                .content("{\"id\": 1," +
                        "\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1," +
                        "\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}"));
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void createUserWithFailEmail() throws Exception {
        var requestBuilder = post("/users")
                .content("/users")
                .content("{\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"mail.ru\", \"birthday\": \"1946-08-20\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"login\": \"login\", " +
                        "\"name\": \"name\",\"email\": \"mail.ru\", \"birthday\": \"1946-08-20\"}"));
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void createUserWithEmptyEmail() throws Exception {
        var requestBuilder = post("/users")
                .content("/users")
                .content("{\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"\", \"birthday\": \"1946-08-20\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"login\": \"login\", " +
                        "\"name\": \"name\",\"email\": \"\", \"birthday\": \"1946-08-20\"}"));
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void createUserWithFailLogin() throws Exception {
        var requestBuilder = post("/users")
                .content("/users")
                .content("{\"login\": \"new login\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"login\": \"new login\", " +
                        "\"name\": \"name\",\"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}"));
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void createUserWithEmptyLogin() throws Exception {
        var requestBuilder = post("/users")
                .content("/users")
                .content("{\"login\": \"\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"login\": \"\", " +
                        "\"name\": \"name\", \"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}"));
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void createUserWithFailBirthday() throws Exception {
        var requestBuilder = post("/users")
                .content("{\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1924-08-20\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1," +
                        "\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1924-08-20\"}"));

    }

    @Test
    public void createUserWithEmptyBody() throws Exception {
        var requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void updateUserWithFailId() throws Exception {
        var requestBuilder = post("/users")
                .content("{\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1," +
                        "\"login\": \"login\", \"name\": \"name\", " +
                        "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-08-20\"}"));

        var requestBuilder1 = put("/users")
                .content("{\"id\": 999," +
                        "\"login\": \"updateLogin\", \"name\": \"updateName\", " +
                        "\"email\": \"mail2@mail.ru\", \"birthday\": \"1946-08-21\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder1)
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 999," +
                        "\"login\": \"updateLogin\", \"name\": \"updateName\", " +
                        "\"email\": \"mail2@mail.ru\", \"birthday\": \"1946-08-21\"}"));
        mockMvc.perform(delete("/users"));
    }
}
