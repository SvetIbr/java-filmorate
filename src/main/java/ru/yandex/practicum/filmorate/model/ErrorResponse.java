package ru.yandex.practicum.filmorate.model;

import org.springframework.stereotype.Component;

//@Component
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
