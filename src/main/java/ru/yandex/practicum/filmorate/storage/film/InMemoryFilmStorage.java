package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
        film.setGenres(new HashSet<>());
        films.put(film.getId(), film);
        numberId++;
        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        films.put(film.getId(), film);
        return film;
    }

    public void deleteAllFilms() {
        films.clear();
        numberId = 1L;
    }

    public Film getFilmById(Long id) {
        return films.getOrDefault(id, null);
    }

    public void addLikeToFilm(Long idFilm, Long idUser) {
        films.get(idFilm).getLikes().add(idUser);
    }

    public void deleteLikeFromFilm(Long idFilm, Long idUser) {
        films.get(idFilm).getLikes().remove(idUser);
    }

    public List<Film> getPopularFilm(Integer count) {
        return films.values().stream()
                .sorted((f0, f1) -> Integer.compare(f1.getLikes().size(), f0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Long> getLikesByFilm(Film film) {
        return films.get(film.getId()).getLikes();
    }

    public boolean checkLike(Long idFilm, Long idUser) {
        return films.get(idFilm).getLikes().contains(idUser);
    }
}
