package ru.sorokin.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sorokin.booking.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByPhone(String phone);
    Optional<User> findByPhone(String phone);
}
