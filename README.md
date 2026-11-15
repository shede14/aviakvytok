# АвіаКвиток — ЛР2–ЛР4 (Spring Boot 4 + PostgreSQL + Vue 3)

ORM-модель і REST-сервіс (backend) системи продажу авіаквитків. Дисципліна «Технології Індустрії 4.0, 5.0 в інформаційних управляючих системах».

## Сутності

`BaseEntity` (@MappedSuperclass: id, createdAt, updatedAt) → `Airline`, `Airport`, `Flight`, `Customer`, `Booking`, `Passenger`, `Ticket`.

- One-to-Many: Airline → Flight, Airport → Flight, Customer → Booking, Booking → Ticket, Passenger → Ticket, Flight → Ticket
- Many-to-Many: Booking ↔ Flight через таблицю `booking_flights`

## Запуск

1. Встановити JDK 17+ і PostgreSQL.
2. Створити базу даних:
   ```sql
   CREATE DATABASE aviakvytok;
   ```
3. За потреби змінити логін і пароль у `src/main/resources/application.properties`.
4. Запустити:
   ```bash
   mvn spring-boot:run
   ```
   або відкрити проєкт в IntelliJ IDEA і запустити `AviakvytokApplication`.

Під час запуску Hibernate створює таблиці (`ddl-auto=create`), а `DataInitializer` додає контрольні записи. Застосунок не має вебчастини, тому після цього завершує роботу — це нормально.

## Перевірка

```sql
SELECT * FROM airlines;
SELECT * FROM airports;
SELECT * FROM flights;
SELECT * FROM customers;
SELECT * FROM passengers;
SELECT * FROM bookings;
SELECT * FROM booking_flights;
SELECT * FROM tickets;
```

## REST API (ЛР3)

Архітектура: Controller → Service → Repository → Entity. Обмін даними у форматі JSON, CORS дозволено для `http://localhost:5173` (Vue.js).

| Ресурс | GET | POST | PUT | DELETE |
| --- | --- | --- | --- | --- |
| `/api/airlines` | список, `/{id}` | створити | `/{id}` оновити | `/{id}` видалити |
| `/api/flights` | список, `/{id}` | створити | `/{id}` оновити | `/{id}` видалити |
| `/api/bookings` | список, `/{id}` | створити | `/{id}` оновити | `/{id}` видалити |

Колекція Postman: `postman/AviaKvytok_LR3.postman_collection.json` (змінна `baseUrl = http://localhost:8080`).

## Клієнтська частина на Vue 3 (ЛР4)

Тека `frontend/`: Vue 3.5 (Composition API), Vite 6, vue-router 4, axios, Bootstrap 5.

Сторінки: Головна, Рейси (CRUD: перегляд, додавання, редагування, видалення), Авіакомпанії, Про проєкт.

Запуск у режимі розробки (потрібен Node.js 18+ і запущений бекенд на порту 8080):

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173
```

Зібрана версія вже лежить у `src/main/resources/static`, тому після запуску Spring Boot застосунок також доступний за адресою http://localhost:8080. Перезібрати її: `npm run build:spring`.
