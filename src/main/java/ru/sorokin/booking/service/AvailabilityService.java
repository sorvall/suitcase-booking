package ru.sorokin.booking.service;

import org.springframework.stereotype.Service;
import ru.sorokin.booking.dto.SuitcaseDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.Statistic;
import ru.sorokin.booking.entity.Suitcase;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.enums.SuitcaseStatus;
import ru.sorokin.booking.repository.BookingRepository;
import ru.sorokin.booking.repository.StatisticRepository;
import ru.sorokin.booking.repository.SuitcaseRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class AvailabilityService {
    private final SuitcaseRepository suitcaseRepository;
    private final BookingRepository bookingRepository;
    private final StatisticRepository statisticRepository;

    public AvailabilityService(SuitcaseRepository suitcaseRepository, BookingRepository bookingRepository, StatisticRepository statisticRepository) {
        this.suitcaseRepository = suitcaseRepository;
        this.bookingRepository = bookingRepository;
        this.statisticRepository = statisticRepository;
    }

    public List<SuitcaseDto> getAvailableSuitcases(LocalDate startDate, LocalDate endDate) {
        statisticRepository.save(new Statistic(1, OffsetDateTime.now(), startDate, endDate));
        List<Suitcase> allAvailable = suitcaseRepository.findAllByStatus(SuitcaseStatus.AVAILABLE);

        List<Booking> conflictingBookings = bookingRepository
                .findBySuitcaseInAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusNotIn(
                        allAvailable, endDate, startDate, List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED)
                );

        Set<UUID> bookedSuitcaseIds = conflictingBookings.stream()
                .map(b -> b.getSuitcase().getId())
                .collect(Collectors.toSet());

        return allAvailable.stream()
                .filter(suitcase -> !bookedSuitcaseIds.contains(suitcase.getId()))
                .map(s -> new SuitcaseDto(
                        s.getId(),
                        s.getSerialNumber(),
                        s.getStatus().name(),
                        s.getLocation(),
                        s.getNotes()
                ))
                .toList();
    }
}
