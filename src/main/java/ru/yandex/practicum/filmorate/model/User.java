package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class User {
    private static int userId = 1;

    private Integer id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неправильный формат электронной почты")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    @NotNull
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