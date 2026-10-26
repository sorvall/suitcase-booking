package ru.sorokin.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sorokin.booking.dto.dashboard.BookingDashboardDto;
import ru.sorokin.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final BookingService bookingService;

    public DashboardController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping()
    public ResponseEntity<List<BookingDashboardDto>> getDashboard() {
        return ResponseEntity.ok(bookingService.getBookingDashboard());
    }
}
