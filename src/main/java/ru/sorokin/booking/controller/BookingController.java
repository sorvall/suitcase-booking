package ru.sorokin.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.dto.BookingResponseDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.service.AvailabilityService;
import ru.sorokin.booking.service.BookingService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private final AvailabilityService availabilityService;
    private final BookingService bookingService;

    public BookingController(AvailabilityService availabilityService, BookingService bookingService) {
        this.availabilityService = availabilityService;
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<UUID> book(@RequestBody BookingRequestDto bookingRequestDto) {
        Booking booking = bookingService.createBooking(bookingRequestDto);
        return ResponseEntity.ok(booking.getId());
    }

    @GetMapping("/check")
    public ResponseEntity<BookingStatus> checkStatus(@RequestParam UUID uuid) {
        Booking booking = bookingService.getBooking(uuid);
        if (Objects.nonNull(booking)) {
            return ResponseEntity.ok(booking.getStatus());
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/phone")
    public ResponseEntity<List<BookingResponseDto>> checkStatusByPhone(@RequestParam String phoneNumber) {
        List<BookingResponseDto> statusByPhoneNumber = bookingService.getStatusByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(statusByPhoneNumber);
    }

}
