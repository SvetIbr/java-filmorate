package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static Long numberId = 1L;

    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film createFilm(Film film) {
        film.setId(numberId);
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        numberId++;
        log.info("Добавлен фильм: " + film);
        return film;
    }

    public Film updateFilm(Film film) {
        checkId(film.getId());
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм: " + film);
        return film;
    }

    public void deleteAllFilms() {
        films.clear();
        numberId = 1L;
    }

    public Film getFilmById(Long id) {
        checkId(id);
        return films.get(id);
    }

    public void addLikeToFilm(Long idFilm, Long idUser) {
        checkId(idFilm);
        films.get(idFilm).getLikes().add(idUser);
        log.info("Фильму " + films.get(idFilm).getName() + " поставили лайк");
    }

    public void deleteLikeFromFilm(Long idFilm, Long idUser) {
        checkId(idFilm);
        films.get(idFilm).getLikes().remove(idUser);
        log.info("У фильма " + films.get(idFilm).getName() + " отменили свой лайк");
    }

    public List<Film> getPopularFilm(Integer count) {
        return films.values().stream()
                .sorted((f0, f1) -> Integer.compare(f1.getLikes().size(), f0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkId(Long id) {
        if (!films.containsKey(id)) {
            log.error("Фильма с таким идентификатором нет");
            throw new NotFoundException("Идентификатор фильма не найден");
        }
    }
}
