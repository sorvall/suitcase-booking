package ru.sorokin.booking.dto.dashboard;

import java.time.LocalDate;

public class BookingDashboardDto {
    private String serial;
    private String name;
    private String phone;
    private LocalDate start;
    private LocalDate end;

    public BookingDashboardDto() {
    }

    public BookingDashboardDto(String serial, String name, String phone, LocalDate start, LocalDate end) {
        this.serial = serial;
        this.name = name;
        this.phone = phone;
        this.start = start;
        this.end = end;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
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
