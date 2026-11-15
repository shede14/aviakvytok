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
import ua.kneu.aviakvytok.dto.AirlineDto;
import ua.kneu.aviakvytok.service.AirlineService;

import java.net.URI;
import java.util.List;

/** REST API авіакомпаній: /api/airlines */
@RestController
@RequestMapping("/api/airlines")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping
    public List<AirlineDto> getAll() {
        return airlineService.findAll();
    }

    @GetMapping("/{id}")
    public AirlineDto getById(@PathVariable Long id) {
        return airlineService.findById(id);
    }

    @PostMapping
    public ResponseEntity<AirlineDto> create(@Valid @RequestBody AirlineDto dto) {
        AirlineDto created = airlineService.create(dto);
        return ResponseEntity.created(URI.create("/api/airlines/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public AirlineDto update(@PathVariable Long id, @Valid @RequestBody AirlineDto dto) {
        return airlineService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        airlineService.delete(id);
    }
}
