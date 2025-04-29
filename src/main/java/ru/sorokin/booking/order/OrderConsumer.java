package ru.sorokin.booking.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.sorokin.booking.configuration.RabbitMQConfiguration;
import ru.sorokin.booking.service.RabbitMQService;

import java.io.IOException;
import java.util.UUID;

@Component
public class OrderConsumer {
    private final ObjectMapper mapper = new ObjectMapper();

    private final RabbitMQService rabbitMQService;

    public OrderConsumer(RabbitMQService rabbitMQService) {
        this.rabbitMQService = rabbitMQService;
    }

    @RabbitListener(queues = RabbitMQConfiguration.DLX_QUEUE)
    public void handleExpiredOrder(Message message) throws IOException {
        UUID bookingId = mapper.readValue(message.getBody(), UUID.class);
        rabbitMQService.handle(bookingId);
    }
}
