package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import ru.yandex.practicum.filmorate.validator.MinimumDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotNull
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @MinimumDate(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительной")
    private int duration;

    @JsonIgnore
    private Set<Long> likes = new HashSet<>();

    public void addLike(Long likeId) {
        likes.add(likeId);
    }

    public void removeLike(Long likeId) {
        likes.remove(likeId);
    }
}
