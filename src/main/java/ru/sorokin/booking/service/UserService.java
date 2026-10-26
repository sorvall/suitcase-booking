package ru.sorokin.booking.service;

import org.springframework.stereotype.Service;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.entity.User;
import ru.sorokin.booking.repository.UserRepository;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreateUser(BookingRequestDto bookingRequestDto) {
        return userRepository.findByPhone(bookingRequestDto.getPhoneNumber())
                .orElseGet(() -> createNewUser(bookingRequestDto));
    }

    private User createNewUser(BookingRequestDto bookingRequestDto) {
        User user = new User();
        user.setFullName(bookingRequestDto.getFullName());
        user.setPhone(bookingRequestDto.getPhoneNumber());
        user.setEmail(UUID.randomUUID().toString());
        user.setKeycloakId(UUID.randomUUID().toString());
        return userRepository.save(user);
    }
}