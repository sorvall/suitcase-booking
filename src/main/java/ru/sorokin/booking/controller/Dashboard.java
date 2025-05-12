package ru.sorokin.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Dashboard {
    @GetMapping("/dashboardPage")
    public String dashboardPage() {
        return "gantt-chart"; // Вернёт gantt-chart.html из папки templates
    }
}
