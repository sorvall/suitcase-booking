package ru.sorokin.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.sorokin.booking.enums.ExtraServiceType;

import java.util.UUID;

@Entity
@Table(name = "extra_service")
public class ExtraService {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExtraServiceType type;

    public ExtraService() {
    }

    public ExtraService(Booking booking, ExtraServiceType type) {
        this.booking = booking;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public ExtraServiceType getType() {
        return type;
    }

    public void setType(ExtraServiceType type) {
        this.type = type;
    }
}