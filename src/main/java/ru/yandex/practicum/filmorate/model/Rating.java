package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Rating {

    private String name;
    public Rating(String name) {
        this.name = name;
    }
}
