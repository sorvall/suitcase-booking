package ru.sorokin.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.dto.BookingResponseDto;
import ru.sorokin.booking.dto.dashboard.BookingDashboardDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.Suitcase;
import ru.sorokin.booking.entity.User;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.enums.ExtraServiceType;
import ru.sorokin.booking.order.OrderProducer;
import ru.sorokin.booking.repository.BookingRepository;
import ru.sorokin.booking.repository.SuitcaseRepository;
import ru.sorokin.booking.repository.UserRepository;
import ru.sorokin.booking.util.NotificationTemplates;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final SuitcaseRepository suitcaseRepository;
    private final UserRepository userRepository;
    private final OrderProducer orderProducer;
    private final ExtraServiceService extraServiceService;

    public BookingService(BookingRepository bookingRepository, SuitcaseRepository suitcaseRepository, UserRepository userRepository, OrderProducer orderProducer, ExtraServiceService extraServiceService) {
        this.bookingRepository = bookingRepository;
        this.suitcaseRepository = suitcaseRepository;
        this.userRepository = userRepository;
        this.orderProducer = orderProducer;
        this.extraServiceService = extraServiceService;
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
        if (bookingRequestDto.isExtraService()) {
            extraServiceService.addExtraService(booking, ExtraServiceType.DELIVERY);
        }
        orderProducer.sendOrderForDelayedCheck(booking.getId());
        orderProducer.sendNotification(getMessage(booking, bookingRequestDto));
        return booking;
    }

    public Booking getBooking(UUID orderUUID) {
        Optional<Booking> order = bookingRepository.findById(orderUUID);
        if (order.isPresent()) {
            return order.get();
        }
        LOGGER.error("Cannot found booking with UUID " + orderUUID);
        return null;
    }

    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }

    public List<BookingResponseDto> getStatusByPhoneNumber(String phoneNumber) {
        return null;
    }

    public List<BookingDashboardDto> getDashboard() {
        return bookingRepository.getDashboard();
    }

    // TODO: сделать рефакторинг
    private String getMessage(Booking booking, BookingRequestDto bookingRequestDto) {
        return String.format(
                NotificationTemplates.BOOKING_CREATED_TEMPLATE,
                booking.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                bookingRequestDto.getPhoneNumber(),
                bookingRequestDto.getFullName(),
                bookingRequestDto.isExtraService() ? "Да" : "Нет"
        );
    }

}

