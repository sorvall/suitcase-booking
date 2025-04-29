package ru.sorokin.booking.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.sorokin.booking.configuration.RabbitMQConfiguration;

import java.util.UUID;

@Service
public class OrderProducer {
    private final ObjectMapper mapper = new ObjectMapper();

    private final RabbitTemplate rabbitTemplate;

    public OrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderForDelayedCheck(UUID bookingId) {
        try {
            Message message = new Message(mapper.writeValueAsBytes(bookingId));
            rabbitTemplate.convertAndSend(
                    RabbitMQConfiguration.ORDER_EXCHANGE,
                    "order",
                    message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}