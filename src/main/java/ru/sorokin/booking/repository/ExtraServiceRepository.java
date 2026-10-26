package ru.sorokin.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sorokin.booking.entity.ExtraService;

import java.util.UUID;

public interface ExtraServiceRepository extends JpaRepository<ExtraService, UUID> {
}
