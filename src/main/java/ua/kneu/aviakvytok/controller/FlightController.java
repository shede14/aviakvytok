package ua.kneu.aviakvytok.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.kneu.aviakvytok.dto.FlightDto;
import ua.kneu.aviakvytok.service.FlightService;

import java.net.URI;
import java.util.List;

/** REST API рейсів: /api/flights */
@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public List<FlightDto> getAll() {
        return flightService.findAll();
    }

    @GetMapping("/{id}")
    public FlightDto getById(@PathVariable Long id) {
        return flightService.findById(id);
    }

    @PostMapping
    public ResponseEntity<FlightDto> create(@Valid @RequestBody FlightDto dto) {
        FlightDto created = flightService.create(dto);
        return ResponseEntity.created(URI.create("/api/flights/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public FlightDto update(@PathVariable Long id, @Valid @RequestBody FlightDto dto) {
        return flightService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        flightService.delete(id);
    }
}
