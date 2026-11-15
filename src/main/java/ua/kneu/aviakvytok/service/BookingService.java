package ua.kneu.aviakvytok.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kneu.aviakvytok.dto.BookingDto;
import ua.kneu.aviakvytok.entity.Booking;
import ua.kneu.aviakvytok.entity.BookingStatus;
import ua.kneu.aviakvytok.entity.Customer;
import ua.kneu.aviakvytok.entity.Flight;
import ua.kneu.aviakvytok.exception.ResourceNotFoundException;
import ua.kneu.aviakvytok.repository.BookingRepository;
import ua.kneu.aviakvytok.repository.CustomerRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/** Бізнес-логіка роботи з бронюваннями (зв'язок Many-to-Many з рейсами). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final FlightService flightService;

    public List<BookingDto> findAll() {
        return bookingRepository.findAll().stream().map(this::toDto).toList();
    }

    public BookingDto findById(Long id) {
        return toDto(getEntity(id));
    }

    @Transactional
    public BookingDto create(BookingDto dto) {
        Booking booking = new Booking();
        apply(booking, dto);
        return toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto update(Long id, BookingDto dto) {
        Booking booking = getEntity(id);
        apply(booking, dto);
        return toDto(bookingRepository.saveAndFlush(booking));
    }

    @Transactional
    public void delete(Long id) {
        bookingRepository.delete(getEntity(id));
    }

    private Booking getEntity(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронювання", id));
    }

    private void apply(Booking booking, BookingDto dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Клієнта", dto.customerId()));

        booking.setPnrCode(dto.pnrCode().toUpperCase());
        booking.setStatus(dto.status() != null ? dto.status() : BookingStatus.NEW);
        booking.setNotes(dto.notes());
        booking.setCustomer(customer);

        booking.getFlights().clear();
        for (Long flightId : new LinkedHashSet<>(dto.flightIds())) {
            booking.getFlights().add(flightService.getEntity(flightId));
        }

        // Вартість: якщо клієнт не передав, рахуємо як суму базових цін рейсів
        BigDecimal total = dto.totalPrice() != null ? dto.totalPrice()
                : booking.getFlights().stream()
                        .map(Flight::getBasePrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        booking.setTotalPrice(total);
    }

    private BookingDto toDto(Booking b) {
        List<Flight> flights = b.getFlights().stream()
                .sorted(Comparator.comparing(Flight::getDepartureTime))
                .toList();
        return new BookingDto(b.getId(), b.getPnrCode(), b.getStatus(), b.getTotalPrice(), b.getNotes(),
                b.getCustomer().getId(), b.getCustomer().getFullName(),
                flights.stream().map(Flight::getId).toList(),
                flights.stream().map(Flight::getFlightNumber).toList(),
                b.getCreatedAt());
    }
}
