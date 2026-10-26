package ru.sorokin.booking.service;

import org.springframework.stereotype.Service;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.ExtraService;
import ru.sorokin.booking.enums.ExtraServiceType;
import ru.sorokin.booking.repository.ExtraServiceRepository;

@Service
public class ExtraServiceService {
    private final ExtraServiceRepository extraServiceRepository;

    public ExtraServiceService(ExtraServiceRepository extraServiceRepository) {
        this.extraServiceRepository = extraServiceRepository;
    }

    public void addExtraService(BookingRequestDto bookingRequestDto, Booking booking, ExtraServiceType extraServiceType) {
        if (bookingRequestDto.isExtraService()) {
            extraServiceRepository.save(new ExtraService(booking, extraServiceType));
        }
    }
}
