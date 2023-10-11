package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class MpaController {
    private final FilmService filmService;

    @GetMapping("/mpa")
    public List<MPA> getAllRatings() {
        return filmService.getAllMPA();
    }

    @GetMapping("/mpa/{id}")
    public MPA getRatingById(@PathVariable int id) {
        return filmService.getMPAById(id);
    }
}
