package ru.sorokin.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sorokin.booking.dto.dashboard.BookingDashboardDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.Suitcase;
import ru.sorokin.booking.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    /**
     * Брони, пересекающиеся с периодом. Сравнения строгие: день окончания одной брони
     * может быть днём начала другой (чемодан вернули и в тот же день выдали снова).
     */
    List<Booking> findBySuitcaseInAndStartDateLessThanAndEndDateGreaterThanAndStatusNotIn(
            List<Suitcase> suitcases,
            LocalDate endDate,
            LocalDate startDate,
            List<BookingStatus> statuses);


    @Query("""
                SELECT new ru.sorokin.booking.dto.dashboard.BookingDashboardDto(
                    s.serialNumber AS serial,
                    COALESCE(u.fullName, 'Нет брони') AS name,
                    COALESCE(u.phone, '') AS phone,
                    b.startDate AS start,
                    b.endDate AS end,
                    CASE WHEN COUNT(CASE WHEN es.type = 'DELIVERY' THEN 1 END) > 0 THEN true ELSE false END AS delivery
                )
                FROM Suitcase s
                LEFT JOIN Booking b ON s.id = b.suitcase.id AND b.status != 'CANCELLED'
                LEFT JOIN User u ON b.user.id = u.id
                LEFT JOIN ExtraService es ON b.id = es.booking.id
                GROUP BY s.serialNumber, u.fullName, u.phone, b.startDate, b.endDate
                ORDER BY s.serialNumber
            """)
    List<BookingDashboardDto> getDashboard();
}
