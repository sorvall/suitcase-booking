package ru.sorokin.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.dto.dashboard.BookingDashboardDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.User;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.enums.ExtraServiceType;
import ru.sorokin.booking.order.OrderProducer;
import ru.sorokin.booking.repository.BookingRepository;
import ru.sorokin.booking.repository.SuitcaseRepository;
import ru.sorokin.booking.repository.UserRepository;
import ru.sorokin.booking.util.NotificationTemplates;

import java.time.LocalDateTime;
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
    private final UserService userService;

    public BookingService(BookingRepository bookingRepository, SuitcaseRepository suitcaseRepository, UserRepository userRepository, OrderProducer orderProducer, ExtraServiceService extraServiceService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.suitcaseRepository = suitcaseRepository;
        this.userRepository = userRepository;
        this.orderProducer = orderProducer;
        this.extraServiceService = extraServiceService;
        this.userService = userService;
    }

    public Booking createBooking(BookingRequestDto bookingRequestDto) {
        User user = userService.getOrCreateUser(bookingRequestDto);
        Booking booking = bookingRepository.save(
                new Booking(
                        user,
                        suitcaseRepository.getReferenceById(bookingRequestDto.getUuid()),
                        bookingRequestDto.getStartDate(),
                        bookingRequestDto.getEndDate(),
                        BookingStatus.PENDING,
                        LocalDateTime.now()
                )
        );

        extraServiceService.addExtraService(bookingRequestDto, booking, ExtraServiceType.DELIVERY);

        orderProducer.sendOrderForDelayedCheck(booking.getId());
        orderProducer.sendNotification(getMessage(booking, bookingRequestDto));
        return booking;
    }

    public Optional<Booking> getBooking(UUID orderUUID) {
        Optional<Booking> order = bookingRepository.findById(orderUUID);
        if (order.isEmpty()) {
            LOGGER.error("Cannot find booking with UUID {}", orderUUID);
        }
        return order;
    }

    public void update(Booking booking) {
        bookingRepository.save(booking);
    }

    public List<BookingDashboardDto> getBookingDashboard() {
        return bookingRepository.getDashboard();
    }

    private String getMessage(Booking booking, BookingRequestDto bookingRequestDto) {
        return String.format(NotificationTemplates.BOOKING_CREATED_TEMPLATE, booking.getId(), booking.getStartDate(), booking.getEndDate(), bookingRequestDto.getPhoneNumber(), bookingRequestDto.getFullName(), bookingRequestDto.isExtraService() ? "Да" : "Нет");
    }

}

