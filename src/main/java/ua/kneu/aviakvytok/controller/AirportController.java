package ua.kneu.aviakvytok.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kneu.aviakvytok.dto.AirportDto;
import ua.kneu.aviakvytok.repository.AirportRepository;

import java.util.List;

/** Довідник аеропортів для клієнтської частини: /api/airports (лише читання). */
@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AirportRepository airportRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public List<AirportDto> getAll() {
        return airportRepository.findAll(Sort.by("iataCode")).stream()
                .map(a -> new AirportDto(a.getId(), a.getIataCode(), a.getName(), a.getCity(), a.getCountry()))
                .toList();
    }
}
