package ru.sorokin.booking.dto.dashboard;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class BookingDashboardDto {
    private UUID id;
    private String name;
    private String phone;
    private LocalDate start;
    private LocalDate end;

    public BookingDashboardDto() {
    }

    public BookingDashboardDto(UUID id, String name, String phone, LocalDate start, LocalDate end) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.start = start;
        this.end = end;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }
}
