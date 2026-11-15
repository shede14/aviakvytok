package ua.kneu.aviakvytok.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ua.kneu.aviakvytok.entity.FlightStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO рейсу. Для створення/оновлення клієнт передає ідентифікатори
 * авіакомпанії та аеропортів; у відповіді додатково повертаються їхні назви.
 */
public record FlightDto(
        Long id,
        @NotBlank String flightNumber,
        @NotNull LocalDateTime departureTime,
        @NotNull LocalDateTime arrivalTime,
        @NotNull @Positive BigDecimal basePrice,
        @NotNull @Min(0) Integer availableSeats,
        FlightStatus status,
        String description,
        @NotNull Long airlineId,
        String airlineName,
        @NotNull Long departureAirportId,
        String departureAirportCode,
        @NotNull Long arrivalAirportId,
        String arrivalAirportCode
) {
}
