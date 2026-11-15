package ua.kneu.aviakvytok.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kneu.aviakvytok.dto.FlightDto;
import ua.kneu.aviakvytok.entity.Airport;
import ua.kneu.aviakvytok.entity.Flight;
import ua.kneu.aviakvytok.entity.FlightStatus;
import ua.kneu.aviakvytok.exception.ResourceNotFoundException;
import ua.kneu.aviakvytok.repository.AirportRepository;
import ua.kneu.aviakvytok.repository.FlightRepository;

import java.util.List;

/** Бізнес-логіка роботи з рейсами. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final AirlineService airlineService;

    public List<FlightDto> findAll() {
        return flightRepository.findAll().stream().map(this::toDto).toList();
    }

    public FlightDto findById(Long id) {
        return toDto(getEntity(id));
    }

    @Transactional
    public FlightDto create(FlightDto dto) {
        Flight flight = new Flight();
        apply(flight, dto);
        return toDto(flightRepository.save(flight));
    }

    @Transactional
    public FlightDto update(Long id, FlightDto dto) {
        Flight flight = getEntity(id);
        apply(flight, dto);
        return toDto(flightRepository.saveAndFlush(flight));
    }

    @Transactional
    public void delete(Long id) {
        flightRepository.delete(getEntity(id));
    }

    Flight getEntity(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Рейс", id));
    }

    private Airport getAirport(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Аеропорт", id));
    }

    private void apply(Flight flight, FlightDto dto) {
        if (!dto.arrivalTime().isAfter(dto.departureTime())) {
            throw new IllegalArgumentException("Час прибуття має бути пізніше часу вильоту");
        }
        if (dto.departureAirportId().equals(dto.arrivalAirportId())) {
            throw new IllegalArgumentException("Аеропорти вильоту та прибуття мають відрізнятися");
        }
        flight.setFlightNumber(dto.flightNumber().toUpperCase());
        flight.setDepartureTime(dto.departureTime());
        flight.setArrivalTime(dto.arrivalTime());
        flight.setBasePrice(dto.basePrice());
        flight.setAvailableSeats(dto.availableSeats());
        flight.setStatus(dto.status() != null ? dto.status() : FlightStatus.SCHEDULED);
        flight.setDescription(dto.description());
        flight.setAirline(airlineService.getEntity(dto.airlineId()));
        flight.setDepartureAirport(getAirport(dto.departureAirportId()));
        flight.setArrivalAirport(getAirport(dto.arrivalAirportId()));
    }

    FlightDto toDto(Flight f) {
        return new FlightDto(f.getId(), f.getFlightNumber(), f.getDepartureTime(), f.getArrivalTime(),
                f.getBasePrice(), f.getAvailableSeats(), f.getStatus(), f.getDescription(),
                f.getAirline().getId(), f.getAirline().getName(),
                f.getDepartureAirport().getId(), f.getDepartureAirport().getIataCode(),
                f.getArrivalAirport().getId(), f.getArrivalAirport().getIataCode());
    }
}
