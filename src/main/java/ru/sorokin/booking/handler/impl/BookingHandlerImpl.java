package ru.sorokin.booking.handler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.handler.BookingHandler;
import ru.sorokin.booking.service.BookingService;

import java.util.UUID;

@Component
public class BookingHandlerImpl implements BookingHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingHandlerImpl.class);

    private final BookingService bookingService;

    public BookingHandlerImpl(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public void handle(UUID bookingId) {
        bookingService.getBooking(bookingId)
                .filter(b -> b.getStatus() == BookingStatus.PENDING)
                .ifPresent(b -> {
                    b.setStatus(BookingStatus.CANCELLED);
                    bookingService.update(b);
                    LOGGER.info("Booking {} cancelled", bookingId);
                });
    }
}
