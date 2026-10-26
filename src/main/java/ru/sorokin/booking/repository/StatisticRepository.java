package ru.sorokin.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sorokin.booking.entity.Statistic;

import java.util.UUID;

public interface StatisticRepository extends JpaRepository<Statistic, UUID> {
}
