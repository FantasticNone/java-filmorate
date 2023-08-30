package ru.yandex.practicum.filmorate.exception;

import java.util.List;

public class BadRequestException extends Throwable {
    private List<String> errors;

    public BadRequestException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
