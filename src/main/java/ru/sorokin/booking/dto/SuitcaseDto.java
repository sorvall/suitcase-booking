package ru.sorokin.booking.dto;

import java.util.UUID;

public class SuitcaseDto {
    private UUID id;
    private String serialNumber;
    private String status;
    private String location;
    private String notes;

    public SuitcaseDto() {
    }

    public SuitcaseDto(UUID id, String serialNumber, String status, String location, String notes) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.status = status;
        this.location = location;
        this.notes = notes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
