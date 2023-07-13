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
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = FilmController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final Film filmWithId = new Film(1, "title", "description",
            LocalDate.of(1994, 1, 18), 180);
    private final Film filmWithoutId = new Film(0, "title", "description",
            LocalDate.of(1994, 1, 18), 180);

    @AfterEach
    public void afterEach() throws Exception {
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void createFilmWithValidFields() throws Exception {
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(filmWithoutId))
                .contentType(MediaType.APPLICATION_JSON)
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
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(filmWithId));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithBlankName() throws Exception {
        Film film = new Film(0, " ", "description",
                LocalDate.of(1994, 1, 18), 180);
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(film));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithEmptyName() throws Exception {
        Film film = new Film(0, "", "description",
                LocalDate.of(1994, 1, 18), 180);
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(film));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithFailDescription() throws Exception {
        Film film = new Film(0, "name", "description description " +
                "description description description description description description " +
                "description description description description description description " +
                "description description description description description description " +
                "description description description", LocalDate.of(1994, 1, 18),
                180);
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(film));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithEmptyDescription() throws Exception {
        Film film = new Film(0, "name", "", LocalDate.of(1994, 1, 18),
                180);
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(film));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithFailReleaseDate() throws Exception {
        Film film = new Film(0, "name", "description",
                LocalDate.of(1893, 1, 18), 180);
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(film));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithFailDuration() throws Exception {
        Film film = new Film(0, "name", "description",
                LocalDate.of(1994, 1, 18), -180);
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(film));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithEmptyDuration() throws Exception {
        Film film = new Film(0, "name", "description",
                LocalDate.of(1994, 1, 18), 0);
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(film));

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createFilmWithEmptyBody() throws Exception {
        var requestBuilder = post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

        getListFilmsMustBeEmpty();
    }

    @Test
    public void createNullFilm() throws Exception {
        Film film = null;
        var requestBuilder = post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest());

        getListFilmsMustBeEmpty();
    }

    @Test
    public void updateFilmWithValidFields() throws Exception {
        createFilm();

        Film updateFilm = new Film(1, "updateTitle", "update description",
                LocalDate.of(1994, 1, 19), 189);
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

        Film updateFilm = new Film(2, "updateTitle", "update description",
                LocalDate.of(1994, 1, 19), 189);
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateFilm));

        getListFilmsMustHaveNotUpdateFilm();
    }

    @Test
    public void updateFilmWithFailName() throws Exception {
        createFilm();

        Film updateFilm = new Film(1, "", "update description",
                LocalDate.of(1994, 1, 19), 189);
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateFilm));

        getListFilmsMustHaveNotUpdateFilm();
    }

    @Test
    public void updateFilmWithFailDescriptions() throws Exception {
        createFilm();

        Film updateFilm = new Film(1, "update title", "",
                LocalDate.of(1994, 1, 19), 189);
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateFilm));

        getListFilmsMustHaveNotUpdateFilm();
    }

    @Test
    public void updateFilmWithFailReleaseDate() throws Exception {
        createFilm();

        Film updateFilm = new Film(1, "update title", "update description",
                LocalDate.of(1888, 1, 19), 189);
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateFilm));

        getListFilmsMustHaveNotUpdateFilm();
    }

    @Test
    public void updateFilmWithFailDuration() throws Exception {
        createFilm();

        Film updateFilm = new Film(1, "update title", "update description",
                LocalDate.of(1888, 1, 19), -189);
        var requestBuilder = put("/films")
                .content(objectMapper.writeValueAsString(updateFilm))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(updateFilm));

        getListFilmsMustHaveNotUpdateFilm();
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