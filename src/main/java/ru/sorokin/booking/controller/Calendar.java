package ru.sorokin.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Calendar {
    @GetMapping("/calendar")
    public String calendarPage() {
        return "calendar";
    }
}
