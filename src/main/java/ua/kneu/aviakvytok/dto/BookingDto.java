package ua.kneu.aviakvytok.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ua.kneu.aviakvytok.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO бронювання. Зв'язок Many-to-Many з рейсами передається
 * списком ідентифікаторів flightIds.
 */
public record BookingDto(
        Long id,
        @NotBlank @Size(min = 6, max = 6) String pnrCode,
        BookingStatus status,
        BigDecimal totalPrice,
        String notes,
        @NotNull Long customerId,
        String customerName,
        @NotEmpty List<Long> flightIds,
        List<String> flightNumbers,
        LocalDateTime createdAt
) {
}
