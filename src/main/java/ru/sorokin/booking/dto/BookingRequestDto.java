package ru.sorokin.booking.dto;

import java.time.LocalDate;
import java.util.UUID;

public class BookingRequestDto {
    private UUID uuid;
    private String fullName;
    private String phoneNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean extraService;

    public BookingRequestDto() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public boolean isExtraService() {
        return extraService;
    }

    public void setExtraService(boolean extraService) {
        this.extraService = extraService;
    }
}
