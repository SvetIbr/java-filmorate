package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class Film {
    @NonNull
    private int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final long duration;

}
