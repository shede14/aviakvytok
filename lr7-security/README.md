# ЛР7. Spring Security: ролі (RBAC) і керування сесіями

Модуль «особистий кабінет» системи «АвіаКвиток» на **Spring Boot 3.5 + Spring Security 6 + Spring Data JPA (Hibernate) + Thymeleaf**,
база **PostgreSQL** (`aviakvytok`, таблиці `users`, `roles`, `users_roles`). Порт **8083**.

| Компонент | Що робить |
|---|---|
| `entity/User`, `entity/Role` | зв'язок Many-to-Many через `@JoinTable(users_roles)`; пароль — BCrypt-хеш |
| `service/UserService` | `UserDetailsService.loadUserByUsername` (ролі → `GrantedAuthority`), реєстрація з `BCryptPasswordEncoder` і роллю `ROLE_USER` |
| `config/SecurityConfig` | `SecurityFilterChain`, `formLogin("/login")`, `logout` → `/`, `maximumSessions(1)`, `SessionRegistry`, `HttpSessionEventPublisher`, сторінка 403 |
| `controller/UserController` | сторінки `/`, `/login`, `/registration`, `/user`, `/manager`, `/admin`, `/access-denied` |
| `config/DataInitializer` | ролі та демо-користувачі |

## Доступ

| Адреса | Хто |
|---|---|
| `/` | усі |
| `/login`, `/registration` | лише неавторизовані |
| `/user/**` | USER, MANAGER, ADMIN |
| `/manager/**` | MANAGER |
| `/admin/**` | ADMIN |

Демо-акаунти: `admin / admin123`, `manager / manager123`, `user / user123`.

## Запуск

`mvn spring-boot:run` → http://localhost:8083 (PostgreSQL: змінні `DB_USER` / `DB_PASSWORD`, за замовчуванням `postgres` / `root`).
