package ru.sorokin.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.dto.BookingResponseDto;
import ru.sorokin.booking.dto.dashboard.BookingDashboardDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.User;
import ru.sorokin.booking.exception.NotFoundException;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.enums.ExtraServiceType;
import ru.sorokin.booking.order.OrderProducer;
import ru.sorokin.booking.push.PushNotificationService;
import ru.sorokin.booking.repository.BookingRepository;
import ru.sorokin.booking.repository.SuitcaseRepository;
import ru.sorokin.booking.repository.UserRepository;
import ru.sorokin.booking.util.NotificationTemplates;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private final PushNotificationService pushNotificationService;

    public BookingService(BookingRepository bookingRepository, SuitcaseRepository suitcaseRepository, UserRepository userRepository, OrderProducer orderProducer, ExtraServiceService extraServiceService, UserService userService, PushNotificationService pushNotificationService) {
        this.bookingRepository = bookingRepository;
        this.suitcaseRepository = suitcaseRepository;
        this.userRepository = userRepository;
        this.orderProducer = orderProducer;
        this.extraServiceService = extraServiceService;
        this.userService = userService;
        this.pushNotificationService = pushNotificationService;
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
        orderProducer.sendNotification(getMessage(booking, bookingRequestDto));
        sendPush(bookingRequestDto);
        return booking;
    }

    public Optional<Booking> getBooking(UUID orderUUID) {
        Optional<Booking> order = bookingRepository.findById(orderUUID);
        if (order.isEmpty()) {
            LOGGER.error("Cannot find booking with UUID {}", orderUUID);
        }
        return order;
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    private BookingResponseDto toResponseDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setSuitcaseId(booking.getSuitcase().getId());
        User user = booking.getUser();
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus().name());
        return dto;
    }

    public void update(Booking booking) {
        bookingRepository.save(booking);
    }

    public Booking confirmBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заказ не найден: " + id));
        if (booking.getStatus() == BookingStatus.IN_USE) {
            return booking;
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Подтвердить можно только заказ в статусе PENDING, сейчас: " + booking.getStatus());
        }
        booking.setStatus(BookingStatus.IN_USE);
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заказ не найден: " + id));
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking;
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя отменить завершённый заказ");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public List<BookingDashboardDto> getBookingDashboard() {
        return bookingRepository.getDashboard();
    }

    private static long rentalDays(BookingRequestDto dto) {
        return ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
    }

    private static long cost(BookingRequestDto dto) {
        return rentalDays(dto) * 500 + (dto.isExtraService() ? 2500 : 0);
    }

    /** Push в PWA админки; ошибка отправки не должна ломать оформление заказа. */
    private void sendPush(BookingRequestDto dto) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM");
            String body = dto.getFullName() + " · " + dto.getStartDate().format(format) + "–" + dto.getEndDate().format(format)
                    + " (" + rentalDays(dto) + " дн.)" + (dto.isExtraService() ? " · доставка" : "") + " · " + cost(dto) + " ₽";
            pushNotificationService.sendToAll("Новый заказ", body, "/admin");
        } catch (RuntimeException e) {
            LOGGER.warn("Cannot send push about new booking: {}", e.toString());
        }
    }

    private String getMessage(Booking booking, BookingRequestDto bookingRequestDto) {
        long daysBetween = rentalDays(bookingRequestDto);
        long cost = cost(bookingRequestDto);
        return String.format(NotificationTemplates.BOOKING_CREATED_TEMPLATE, booking.getId(), booking.getStartDate(), booking.getEndDate(), daysBetween, bookingRequestDto.getPhoneNumber().replaceAll("\\s+", ""), bookingRequestDto.getFullName(), bookingRequestDto.isExtraService() ? "Да" : "Нет", cost);
    }

}

