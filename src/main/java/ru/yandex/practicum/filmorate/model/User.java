package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class User {
    private static int userId = 1;

    private Integer id;
    private String email;
    private String login;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    public User(String email, String login, String name, LocalDate birthday) {
        this.id = userId();
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public static synchronized int userId() {
        return userId++;
    }
}