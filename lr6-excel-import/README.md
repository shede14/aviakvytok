# ЛР6. Імпорт продажів з Excel і аналітика (Spring Boot 3 + Apache POI + Vue 3 + Chart.js)

Модуль системи «АвіаКвиток»: користувач завантажує таблицю Excel із продажами квитків, сервер розбирає її
за допомогою **Apache POI** (`XSSFWorkbook`) і зберігає записи в **PostgreSQL** (`saveAll`), а клієнт на **Vue 3**
одразу оновлює графіки **Chart.js**.

| Частина | Що зроблено |
|---|---|
| `entity/TicketSale` | сутність продажу (код бронювання, пасажир, рейс, напрямок, дата, клас, статус, ціна) |
| `service/ExcelService` | `MultipartFile` → `InputStream` → `XSSFWorkbook`, ітерація з другого рядка, валідація клітинок, пропуск дублікатів, `saveAll` |
| `service/StatisticsService` | агреговані дані: кількість за датами, напрямками, класами, статусами |
| `controller/ImportController` | `POST /api/import/excel` (multipart, поле `file`), `GET /api/stats`, `GET /api/sales/recent`, `DELETE /api/sales` |
| `frontend/` | Vue 3 + Vite + axios (FormData) + Chart.js (vue-chartjs): `ImportPanel.vue`, `AnalyticsChart.vue` (Bar / Pie), `RecentTable.vue` |

Шаблон: [`frontend/public/template.xlsx`](frontend/public/template.xlsx) (також доступний на сторінці — «Завантажити шаблон»).
Демо-файли: `frontend/public/samples/` (другий файл містить 2 помилкові рядки та дублікат — для перевірки обробки помилок).

## Запуск

1. PostgreSQL з базою `aviakvytok` (та сама, що й у ЛР2–ЛР4; таблиця `ticket_sales` створюється автоматично).
   Логін/пароль — змінні `DB_USER` / `DB_PASSWORD` (за замовчуванням `postgres` / `root`).
2. `mvn spring-boot:run` → http://localhost:8082 (зібраний клієнт уже лежить у `src/main/resources/static`).
3. Розробка клієнта: `cd frontend && npm install && npm run dev` (http://localhost:5174, `/api` проксіюється на 8082);
   `npm run build:spring` — зібрати клієнт у `src/main/resources/static`.
