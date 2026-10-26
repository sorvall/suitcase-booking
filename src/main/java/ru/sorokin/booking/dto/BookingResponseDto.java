package ru.sorokin.booking.dto;

import java.time.LocalDate;
import java.util.UUID;

public class BookingResponseDto {
    private UUID id;
    private UUID suitcaseId;
    private String fullName;
    private String phone;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public BookingResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSuitcaseId() {
        return suitcaseId;
    }

    public void setSuitcaseId(UUID suitcaseId) {
        this.suitcaseId = suitcaseId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
