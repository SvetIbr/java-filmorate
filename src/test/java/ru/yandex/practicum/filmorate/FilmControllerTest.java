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
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createFilmWithValidFieldsAndUpdateWithValidFields() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \"name\",\"description\": \"description\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1,\"name\": \"name\"," +
                        "\"description\": \"description\",\"releaseDate\": \"1994-01-18\",\"duration\": 180}"));

        var requestBuilder1 = put("/films")
                .content("{\"id\": 1,\"name\": \"updateName\",\"description\": \"updateDescription\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder1)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1,\"name\": \"updateName\"," +
                        "\"description\": \"updateDescription\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}"));
        mockMvc.perform(delete("/films"));
    }
    @Test
    public void createFilmWithId() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"id\": 1,\"name\": \"name\"," +
                        "\"description\": \"description\",\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1,\"name\": \"name\"," +
                        "\"description\": \"description\",\"releaseDate\": \"1994-01-18\",\"duration\": 180}"));
        mockMvc.perform(delete("/films"));
    }
    @Test
    public void createFilmWithFailName() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \" \"," +
                        "\"description\": \"description\",\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"name\": \" \"," +
                        "\"description\": \"description\",\"releaseDate\": \"1994-01-18\",\"duration\": 180}"));
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void createFilmWithFailDescription() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \"name\"," +
                        "\"description\": \"description description description " +
                        "description description description description " +
                        "description description description description " +
                        "description description description description " +
                        "description description description description " +
                        "description description description description\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"name\": \"name\"," +
                        "\"description\": \"description description description " +
                        "description description description description " +
                        "description description description description " +
                        "description description description description " +
                        "description description description description " +
                        "description description description description\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 180}"));
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void createFilmWithFailReleaseDate() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \"name\"," +
                        "\"description\": \"description\",\"releaseDate\": \"1832-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"name\": \"name\"," +
                        "\"description\": \"description\",\"releaseDate\": \"1832-01-18\",\"duration\": 180}"));
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void createFilmWithFailDuration() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \"name\"," +
                        "\"description\": \"description\",\"releaseDate\": \"1994-01-18\",\"duration\": -180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"name\": \"name\"," +
                        "\"description\": \"description\",\"releaseDate\": \"1994-01-18\",\"duration\": -180}"));
        mockMvc.perform(delete("/films"));
    }
    @Test
    public void createFilmWithEmptyBody() throws Exception {
        var requestBuilder = post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/films"));
    }

//    @Test
//    public void updateFilmWithValidFields() throws Exception {
//        var requestBuilder = post("/films")
//                .content("{\"name\": \"name\",\"description\": \"description\"," +
//                        "\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON);
//
//        mockMvc.perform(requestBuilder);
//
//        var requestBuilder1 = put("/films")
//                .content("{\"id\": 1,\"name\": \"updateName\",\"description\": \"updateDescription\"," +
//                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON);
//
//        mockMvc.perform(requestBuilder1)
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1,\"name\": \"updateName\"," +
//                        "\"description\": \"updateDescription\"," +
//                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}"));
//    }

    @Test
    public void updateFilmWithFailId() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \"name\",\"description\": \"description\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);

        var requestBuilder1 = put("/films")
                .content("{\"id\": 999,\"name\": \"updateName\",\"description\": \"updateDescription\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder1)
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 999,\"name\": \"updateName\"," +
                        "\"description\": \"updateDescription\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}"));
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void updateFilmWithFailName() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \"name\",\"description\": \"description\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);

        var requestBuilder1 = put("/films")
                .content("{\"id\": 1,\"name\": \" \",\"description\": \"updateDescription\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder1)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1,\"name\": \" \"," +
                        "\"description\": \"updateDescription\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}"));
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void updateFilmWithFailDescriptions() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \"name\",\"description\": \"description\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);

        var requestBuilder1 = put("/films")
                .content("{\"id\": 1,\"name\": \"name\",\"description\": \"\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder1)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"id\": 1,\"name\": \"name\"," +
                        "\"description\": \"\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 189}"));
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void updateFilmWithFailReleaseDate() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \"name\",\"description\": \"description\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);

        var requestBuilder1 = put("/films")
                .content("{\"name\": \"name\",\"description\": \"description\"," +
                        "\"releaseDate\": \"1824-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder1)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"name\": \"name\"," +
                        "\"description\": \"description\"," +
                        "\"releaseDate\": \"1824-01-18\",\"duration\": 180}"));
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void updateFilmWithFailDuration() throws Exception {
        var requestBuilder = post("/films")
                .content("{\"name\": \"name\",\"description\": \"description\"," +
                        "\"releaseDate\": \"1994-01-18\",\"duration\": 180}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);

        var requestBuilder1 = put("/films")
                .content("{\"name\": \"name\",\"description\": \"description\"," +
                        "\"releaseDate\": \"1824-01-18\",\"duration\": 0}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder1)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("{\"name\": \"name\"," +
                        "\"description\": \"description\"," +
                        "\"releaseDate\": \"1824-01-18\",\"duration\": 0}"));
        mockMvc.perform(delete("/films"));
    }


}