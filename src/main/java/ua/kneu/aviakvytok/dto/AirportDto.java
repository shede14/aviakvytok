package ua.kneu.aviakvytok.dto;

/** DTO аеропорту (довідник для форми рейсу на фронтенді). */
public record AirportDto(
        Long id,
        String iataCode,
        String name,
        String city,
        String country
) {
}
