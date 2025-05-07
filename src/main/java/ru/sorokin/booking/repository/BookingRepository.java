package ru.sorokin.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sorokin.booking.dto.dashboard.BookingDashboardDto;
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


    @Query("""
        SELECT new ru.sorokin.booking.dto.dashboard.BookingDashboardDto(
            s.serialNumber AS serial,
            u.fullName AS name,
            u.phone AS phone,
            b.startDate AS start,
            b.endDate AS end
        )
        FROM Suitcase s
        JOIN Booking b ON s.id = b.suitcase.id
        JOIN User u ON b.user.id = u.id
        WHERE b.status != 'CANCELLED'
        """)
    List<BookingDashboardDto> getDashboard();
}
