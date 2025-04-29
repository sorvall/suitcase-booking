package ru.sorokin.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.Suitcase;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.enums.SuitcaseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findBySuitcaseInAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusNotIn(
            List<Suitcase> suitcases,
            LocalDate endDate,
            LocalDate startDate,
            List<BookingStatus> statuses);
}
