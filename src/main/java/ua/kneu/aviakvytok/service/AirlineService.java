package ua.kneu.aviakvytok.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kneu.aviakvytok.dto.AirlineDto;
import ua.kneu.aviakvytok.entity.Airline;
import ua.kneu.aviakvytok.exception.ResourceNotFoundException;
import ua.kneu.aviakvytok.repository.AirlineRepository;

import java.util.List;

/** Бізнес-логіка роботи з авіакомпаніями. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirlineService {

    private final AirlineRepository airlineRepository;

    public List<AirlineDto> findAll() {
        return airlineRepository.findAll().stream().map(this::toDto).toList();
    }

    public AirlineDto findById(Long id) {
        return toDto(getEntity(id));
    }

    @Transactional
    public AirlineDto create(AirlineDto dto) {
        Airline airline = new Airline();
        apply(airline, dto);
        return toDto(airlineRepository.save(airline));
    }

    @Transactional
    public AirlineDto update(Long id, AirlineDto dto) {
        Airline airline = getEntity(id);
        apply(airline, dto);
        return toDto(airlineRepository.saveAndFlush(airline));
    }

    @Transactional
    public void delete(Long id) {
        airlineRepository.delete(getEntity(id));
    }

    Airline getEntity(Long id) {
        return airlineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Авіакомпанію", id));
    }

    private void apply(Airline airline, AirlineDto dto) {
        airline.setIataCode(dto.iataCode().toUpperCase());
        airline.setName(dto.name());
        airline.setCountry(dto.country());
        airline.setDescription(dto.description());
    }

    private AirlineDto toDto(Airline a) {
        return new AirlineDto(a.getId(), a.getIataCode(), a.getName(), a.getCountry(),
                a.getDescription(), a.getFlights().size(), a.getCreatedAt(), a.getUpdatedAt());
    }
}
