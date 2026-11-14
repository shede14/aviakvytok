package ua.kneu.aviakvytok.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.kneu.aviakvytok.entity.Airline;
import ua.kneu.aviakvytok.entity.Airport;
import ua.kneu.aviakvytok.entity.Booking;
import ua.kneu.aviakvytok.entity.BookingStatus;
import ua.kneu.aviakvytok.entity.Customer;
import ua.kneu.aviakvytok.entity.Flight;
import ua.kneu.aviakvytok.entity.Passenger;
import ua.kneu.aviakvytok.entity.Ticket;
import ua.kneu.aviakvytok.repository.AirlineRepository;
import ua.kneu.aviakvytok.repository.AirportRepository;
import ua.kneu.aviakvytok.repository.BookingRepository;
import ua.kneu.aviakvytok.repository.CustomerRepository;
import ua.kneu.aviakvytok.repository.FlightRepository;
import ua.kneu.aviakvytok.repository.PassengerRepository;
import ua.kneu.aviakvytok.repository.TicketRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Додає контрольні записи в кожну таблицю під час запуску застосунку. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;
    private final CustomerRepository customerRepository;
    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (airlineRepository.count() > 0) {
            return; // дані вже додано (режим ddl-auto=update)
        }

        // Авіакомпанії
        Airline lot = airline("LO", "LOT Polish Airlines", "Польща",
                "Національний перевізник Польщі, базовий аеропорт - Варшава.");
        Airline lufthansa = airline("LH", "Lufthansa", "Німеччина",
                "Найбільша авіакомпанія Німеччини, хаби у Франкфурті та Мюнхені.");

        // Аеропорти
        Airport waw = airport("WAW", "Warsaw Chopin Airport", "Варшава", "Польща");
        Airport fra = airport("FRA", "Frankfurt Airport", "Франкфурт", "Німеччина");
        Airport muc = airport("MUC", "Munich Airport", "Мюнхен", "Німеччина");

        // Рейси
        Flight wawFra = flight("LO379", lot, waw, fra,
                LocalDateTime.of(2026, 12, 1, 7, 30), LocalDateTime.of(2026, 12, 1, 9, 25),
                "4200.00", 120, "Ранковий рейс Варшава - Франкфурт.");
        Flight fraMuc = flight("LH104", lufthansa, fra, muc,
                LocalDateTime.of(2026, 12, 1, 11, 0), LocalDateTime.of(2026, 12, 1, 11, 55),
                "3100.00", 150, "Стикувальний рейс Франкфурт - Мюнхен.");

        // Клієнти
        Customer olena = customer("olena.koval@example.com", "Коваль Олена", "+380501112233");
        Customer andrii = customer("andrii.melnyk@example.com", "Мельник Андрій", "+380672223344");

        // Пасажири
        Passenger p1 = passenger("Olena", "Koval", "FA123456", LocalDate.of(1995, 4, 12));
        Passenger p2 = passenger("Andrii", "Melnyk", "FB654321", LocalDate.of(1988, 9, 3));

        // Бронювання: перше містить два рейси (зв'язок Many-to-Many)
        Booking b1 = booking("AB12CD", olena, BookingStatus.TICKETED, "7300.00",
                "Маршрут зі стикуванням у Франкфурті.");
        b1.getFlights().add(wawFra);
        b1.getFlights().add(fraMuc);
        bookingRepository.save(b1);

        Booking b2 = booking("EF34GH", andrii, BookingStatus.PAID, "4200.00",
                "Прямий рейс, багаж 23 кг.");
        b2.getFlights().add(wawFra);
        bookingRepository.save(b2);

        // Квитки
        ticket("0802400000001", "12A", "4200.00", b1, p1, wawFra);
        ticket("2202400000002", "7C", "3100.00", b1, p1, fraMuc);
        ticket("0802400000003", "14F", "4200.00", b2, p2, wawFra);
    }

    private Airline airline(String code, String name, String country, String description) {
        Airline a = new Airline();
        a.setIataCode(code);
        a.setName(name);
        a.setCountry(country);
        a.setDescription(description);
        return airlineRepository.save(a);
    }

    private Airport airport(String code, String name, String city, String country) {
        Airport a = new Airport();
        a.setIataCode(code);
        a.setName(name);
        a.setCity(city);
        a.setCountry(country);
        return airportRepository.save(a);
    }

    private Flight flight(String number, Airline airline, Airport from, Airport to,
                          LocalDateTime departure, LocalDateTime arrival,
                          String price, int seats, String description) {
        Flight f = new Flight();
        f.setFlightNumber(number);
        f.setAirline(airline);
        f.setDepartureAirport(from);
        f.setArrivalAirport(to);
        f.setDepartureTime(departure);
        f.setArrivalTime(arrival);
        f.setBasePrice(new BigDecimal(price));
        f.setAvailableSeats(seats);
        f.setDescription(description);
        return flightRepository.save(f);
    }

    private Customer customer(String email, String fullName, String phone) {
        Customer c = new Customer();
        c.setEmail(email);
        c.setFullName(fullName);
        c.setPhone(phone);
        return customerRepository.save(c);
    }

    private Passenger passenger(String firstName, String lastName, String passport, LocalDate birthDate) {
        Passenger p = new Passenger();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setPassportNumber(passport);
        p.setBirthDate(birthDate);
        return passengerRepository.save(p);
    }

    private Booking booking(String pnr, Customer customer, BookingStatus status, String total, String notes) {
        Booking b = new Booking();
        b.setPnrCode(pnr);
        b.setCustomer(customer);
        b.setStatus(status);
        b.setTotalPrice(new BigDecimal(total));
        b.setNotes(notes);
        return b;
    }

    private void ticket(String number, String seat, String price,
                        Booking booking, Passenger passenger, Flight flight) {
        Ticket t = new Ticket();
        t.setTicketNumber(number);
        t.setSeatNumber(seat);
        t.setPrice(new BigDecimal(price));
        t.setBooking(booking);
        t.setPassenger(passenger);
        t.setFlight(flight);
        ticketRepository.save(t);
    }
}
