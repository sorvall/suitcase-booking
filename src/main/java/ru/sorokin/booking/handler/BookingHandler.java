package ru.sorokin.booking.handler;

import java.util.UUID;

public interface BookingHandler {
    void handle(UUID bookingId);
}
