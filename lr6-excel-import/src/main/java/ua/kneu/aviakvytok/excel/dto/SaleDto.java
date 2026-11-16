package ua.kneu.aviakvytok.excel.dto;

import ua.kneu.aviakvytok.excel.entity.TicketSale;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Рядок таблиці останніх імпортованих продажів. */
public record SaleDto(Long id, String bookingCode, String passengerName, String flightNumber,
                      String direction, LocalDate saleDate, String fareClass, String status,
                      BigDecimal price, String importFile) {

    public static SaleDto from(TicketSale s) {
        return new SaleDto(s.getId(), s.getBookingCode(), s.getPassengerName(), s.getFlightNumber(),
                s.getDirection(), s.getSaleDate(), s.getFareClass().getLabel(), s.getStatus().getLabel(),
                s.getPrice(), s.getImportFile());
    }
}
