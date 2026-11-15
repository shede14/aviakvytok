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
import ua.kneu.aviakvytok.dto.BookingDto;
import ua.kneu.aviakvytok.service.BookingService;

import java.net.URI;
import java.util.List;

/** REST API бронювань: /api/bookings */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getAll() {
        return bookingService.findAll();
    }

    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @PostMapping
    public ResponseEntity<BookingDto> create(@Valid @RequestBody BookingDto dto) {
        BookingDto created = bookingService.create(dto);
        return ResponseEntity.created(URI.create("/api/bookings/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public BookingDto update(@PathVariable Long id, @Valid @RequestBody BookingDto dto) {
        return bookingService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookingService.delete(id);
    }
}
