/*
package ru.sorokin.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sorokin.booking.dto.BookingRequestDto;
import ru.sorokin.booking.service.ExtraServiceService;

import java.util.UUID;

@RestController
@RequestMapping("/extras")
public class ExtraServicesController {
    private final ExtraServiceService extraService;

    public ExtraServicesController(ExtraServiceService extraService) {
        this.extraService = extraService;
    }

    @PostMapping
    public ResponseEntity<UUID> book(@RequestBody BookingRequestDto bookingRequestDto) {
        extraService.addExtraService(bookingRequestDto);
        return ResponseEntity.ok(booking.getId());
    }
}
*/
