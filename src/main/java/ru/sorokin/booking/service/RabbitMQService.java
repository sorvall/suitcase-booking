package ru.sorokin.booking.service;

import org.springframework.stereotype.Component;
import ru.sorokin.booking.handler.BookingHandler;

import java.util.List;
import java.util.UUID;

@Component
public class RabbitMQService {
    private final List<BookingHandler> handlers;

    public RabbitMQService(List<BookingHandler> handlers) {
        this.handlers = handlers;
    }

    public void handle(UUID bookingId) {
        handlers.forEach(handler -> handler.handle(bookingId));
    }
}
