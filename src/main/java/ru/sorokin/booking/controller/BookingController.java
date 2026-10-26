package ru.sorokin.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.dto.BookingResponseDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.service.BookingService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
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

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingStatus> confirm(@PathVariable UUID id) {
        Booking booking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(booking.getStatus());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingStatus> cancel(@PathVariable UUID id) {
        Booking booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(booking.getStatus());
    }

}
