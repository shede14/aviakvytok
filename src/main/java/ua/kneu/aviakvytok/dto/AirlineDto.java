package ua.kneu.aviakvytok.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** DTO авіакомпанії: використовується і для запитів, і для відповідей. */
public record AirlineDto(
        Long id,
        @NotBlank @Size(min = 2, max = 2) String iataCode,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 60) String country,
        String description,
        Integer flightsCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
