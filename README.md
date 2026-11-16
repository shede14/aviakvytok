# АвіаКвиток — ЛР2–ЛР7 (Spring Boot + PostgreSQL + Vue 3 + Spring AI + Spring Security)

Система продажу авіаквитків: ORM-модель (ЛР2), REST-сервіс (ЛР3), клієнт на Vue 3 (ЛР4), AI-модуль на Spring AI (ЛР5, `lr5-spring-ai`), імпорт з Excel і графіки (ЛР6, `lr6-excel-import`) та Spring Security (ЛР7, `lr7-security`). Дисципліна «Технології Індустрії 4.0, 5.0 в інформаційних управляючих системах».

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

## ЛР5. Інтеграція Generative AI (Spring AI)

Окремий модуль [`lr5-spring-ai/`](lr5-spring-ai/) на Spring Boot 3.5 + Spring AI 1.0 (`OpenAiChatModel`, `PromptTemplate`):
структурування неструктурованого бібліографічного опису статті в JSON і Java Record `SaveArticleToEntity` (Jackson).
Сторінка Thymeleaf — http://localhost:8081, REST — `POST /api/generate-all`. Ключ — змінна середовища `OPENAI_API_KEY`;
за замовчуванням використовується OpenAI-сумісний API Groq (модель `openai/gpt-oss-120b`). Детальніше — у [README модуля](lr5-spring-ai/README.md).

## ЛР6. Імпорт з Excel і аналітика

Модуль [`lr6-excel-import/`](lr6-excel-import/): Spring Boot 3 + Apache POI (`XSSFWorkbook`, `saveAll`) + PostgreSQL; клієнт Vue 3 + axios (FormData) + Chart.js
(Bar / Pie). http://localhost:8082, `POST /api/import/excel`, `GET /api/stats`. Шаблон — `frontend/public/template.xlsx`. Детальніше — у [README модуля](lr6-excel-import/README.md).

## ЛР7. Spring Security

Модуль [`lr7-security/`](lr7-security/): Spring Boot 3 + Spring Security + Hibernate + Thymeleaf. Ролі USER / MANAGER / ADMIN (Many-to-Many),
BCrypt, власна сторінка входу, `/admin/**`, `/manager/**`, `/user/**`, одна активна сесія на користувача (`SessionRegistry`).
http://localhost:8083. Детальніше — у [README модуля](lr7-security/README.md).
