package ru.sorokin.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.service.BookingService;

import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<UUID> book(@RequestBody BookingRequestDto bookingRequestDto) {
        Booking booking = bookingService.createBooking(bookingRequestDto);
        return ResponseEntity.ok(booking.getId());
    }

    @GetMapping("/check")
    public ResponseEntity<BookingStatus> checkStatus(@RequestParam UUID uuid) {
        return bookingService.getBooking(uuid).map(b -> ResponseEntity.ok(b.getStatus())).orElseGet(() -> ResponseEntity.noContent().build());
    }

}
