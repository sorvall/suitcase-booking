package ru.sorokin.booking.service;

import org.springframework.stereotype.Service;
import ru.sorokin.booking.dto.SuitcaseDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.Suitcase;
import ru.sorokin.booking.enums.BookingStatus;
import ru.sorokin.booking.enums.SuitcaseStatus;
import ru.sorokin.booking.repository.BookingRepository;
import ru.sorokin.booking.repository.SuitcaseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class AvailabilityService {
    private final SuitcaseRepository suitcaseRepository;
    private final BookingRepository bookingRepository;

    public AvailabilityService(SuitcaseRepository suitcaseRepository, BookingRepository bookingRepository) {
        this.suitcaseRepository = suitcaseRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<SuitcaseDto> getAvailableSuitcases(LocalDate startDate, LocalDate endDate) {
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
