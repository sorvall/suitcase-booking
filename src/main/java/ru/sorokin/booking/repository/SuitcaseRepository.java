package ru.sorokin.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sorokin.booking.entity.Suitcase;
import ru.sorokin.booking.enums.SuitcaseStatus;

import java.util.List;
import java.util.UUID;

public interface SuitcaseRepository extends JpaRepository<Suitcase, UUID> {
    List<Suitcase> findAllByStatus (SuitcaseStatus status);
}
