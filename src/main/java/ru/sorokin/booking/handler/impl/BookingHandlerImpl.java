package ru.sorokin.booking.handler.impl;

import org.springframework.stereotype.Component;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.handler.BookingHandler;
import ru.sorokin.booking.service.BookingService;

import java.util.UUID;

@Component
public class BookingHandlerImpl implements BookingHandler {
    private final BookingService bookingService;

    public BookingHandlerImpl(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public void handle(UUID bookingId) {
        Booking booking = bookingService.getBooking(bookingId);
        if (booking.getStatus().equals(BookingStatus.PENDING)) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingService.update(booking);
        }
    }
}
