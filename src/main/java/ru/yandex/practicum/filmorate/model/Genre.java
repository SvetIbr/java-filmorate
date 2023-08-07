package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Genre {

    private String name;
    public Genre(String name) {
        this.name = name;
    }
}
