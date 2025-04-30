package ru.sorokin.booking.service;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.dto.BookingResponseDto;
import ru.sorokin.booking.dto.SuitcaseDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.Suitcase;
import ru.sorokin.booking.entity.User;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.exception.NotFoundException;
import ru.sorokin.booking.order.OrderProducer;
import ru.sorokin.booking.repository.BookingRepository;
import ru.sorokin.booking.repository.SuitcaseRepository;
import ru.sorokin.booking.repository.UserRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {
    private final AvailabilityService availabilityService;
    private final BookingRepository bookingRepository;
    private final SuitcaseRepository suitcaseRepository;
    private final UserRepository userRepository;
    private final OrderProducer orderProducer;

    public BookingService(AvailabilityService availabilityService, BookingRepository bookingRepository, SuitcaseRepository suitcaseRepository, UserRepository userRepository, OrderProducer orderProducer) {
        this.availabilityService = availabilityService;
        this.bookingRepository = bookingRepository;
        this.suitcaseRepository = suitcaseRepository;
        this.userRepository = userRepository;
        this.orderProducer = orderProducer;
    }

    public Booking createBooking(BookingRequestDto bookingRequestDto) {
        if (!userRepository.existsByPhone(bookingRequestDto.getPhoneNumber())) {
            User user = new User();
            user.setFullName(bookingRequestDto.getFullName());
            user.setPhone(bookingRequestDto.getPhoneNumber());
            user.setEmail(UUID.randomUUID().toString());
            user.setKeycloakId(UUID.randomUUID().toString());
            userRepository.save(user);
        }
        User user = userRepository.findByPhone(bookingRequestDto.getPhoneNumber()).get();
        Suitcase suitcase = suitcaseRepository.findById(bookingRequestDto.getUuid()).get();
        Booking booking = bookingRepository.save(
                new Booking(user,
                        suitcase,
                        bookingRequestDto.getStartDate(),
                        bookingRequestDto.getEndDate(),
                        BookingStatus.PENDING,
                        OffsetDateTime.now())
        );
        orderProducer.sendOrderForDelayedCheck(booking.getId());
        orderProducer.sendNotification("Оформлен новый заказ " + booking.getId() + " " + bookingRequestDto.getPhoneNumber() + " " + bookingRequestDto.getFullName());
        return booking;
    }

/*    public Booking getStatus(UUID orderUUID) {
        Optional<Booking> order = bookingRepository.findById(orderUUID);
        if (order.isPresent()) {
            return order.get();
        }
        throw new NotFoundException("Cannot found booking with UUID " + orderUUID);
    }*/

    public Booking getBooking(UUID orderUUID) {
        Optional<Booking> order = bookingRepository.findById(orderUUID);
        if (order.isPresent()) {
            return order.get();
        }
        throw new NotFoundException("Cannot found booking with UUID " + orderUUID);
    }

    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }

    public List<BookingResponseDto> getStatusByPhoneNumber(String phoneNumber) {
        return null;
    }
}

