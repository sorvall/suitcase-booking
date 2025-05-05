package ru.sorokin.booking.service;

import org.springframework.stereotype.Service;
import ru.sorokin.booking.entity.Booking;
import ru.sorokin.booking.entity.ExtraService;
import ru.sorokin.booking.enums.ExtraServiceType;
import ru.sorokin.booking.repository.ExtraServiceRepository;

import java.util.Objects;

@Service
public class ExtraServiceService {
    private final ExtraServiceRepository extraServiceRepository;

    public ExtraServiceService(ExtraServiceRepository extraServiceRepository) {
        this.extraServiceRepository = extraServiceRepository;
    }

    public ExtraService addExtraService(Booking booking, ExtraServiceType extraServiceType) {
        if (Objects.nonNull(extraServiceRepository)) {
            return extraServiceRepository.save(new ExtraService(booking, extraServiceType));
        }
        return null;
    }
}
