package ru.sorokin.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sorokin.booking.dto.SuitcaseDto;
import ru.sorokin.booking.service.AvailabilityService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public ResponseEntity<List<SuitcaseDto>> getAvailabilitySuitcases(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<SuitcaseDto> availableSuitcases = availabilityService.getAvailableSuitcases(
                startDate,
                endDate);
        return ResponseEntity.ok(availableSuitcases);
    }

}
