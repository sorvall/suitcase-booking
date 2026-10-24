package ru.sorokin.booking.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.sorokin.booking.configuration.RabbitMQConfiguration;
import ru.sorokin.booking.service.RabbitMQService;

import java.io.IOException;
import java.util.UUID;

@Component
public class OrderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private final RabbitMQService rabbitMQService;

    public OrderConsumer(RabbitMQService rabbitMQService) {
        this.rabbitMQService = rabbitMQService;
    }

    @RabbitListener(queues = RabbitMQConfiguration.DLX_QUEUE)
    public void handleExpiredOrder(Message message) throws IOException {
        LOGGER.info("Received expired order");
        UUID bookingId = mapper.readValue(message.getBody(), UUID.class);
        LOGGER.info("Booking id is {}", bookingId);
        rabbitMQService.handle(bookingId);
    }
}
