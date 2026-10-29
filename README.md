# Spring Library

Приложение-библиотека: хранение книг, авторов, жанров и комментариев в реляционной БД. REST API построен на HATEOAS-принципах через Spring Data REST: репозитории Author, Genre, Book, Comment автоматически экспонированы как REST-ресурсы (/api/authors, /api/genres, /api/books, /api/comments) с HATEOAS-ссылками (_links). Авторизация на JWT (Spring Security): клиент получает токен на /api/auth/login и передает его в заголовке Authorization: Bearer <token>, ролевые ограничения - через @PreAuthorize, включены Spring Boot Actuator: метрики, healthchecks и logfile, включая собственный HealthCheck-индикатор.

## Технологии

- Spring Boot 3.3, Spring MVC (REST API), JPA + Hibernate (Hibernate — только как JPA-провайдер), `EntityManager`, Hibernate-аннотация `@Fetch` (`FetchMode.SUBSELECT`) для решения N+1 для связи книга → комментарии
- DTO (`ru.otus.spring.dto`) вместо JPA-сущностей в REST-слое — контроллеры отдают/принимают DTO
- Springdoc OpenAPI (Swagger UI) — интерактивная документация REST API
- H2 — in-memory реляционная БД
- Liquibase — автоматическая проливка схемы и начальных данных при старте (DDL через Hibernate отключён: `spring.jpa.hibernate.ddl-auto: none`)
- Spring Data REST (spring-boot-starter-data-rest) — REST-слой на HATEOAS-принципах: репозитории `Author`, `Genre`, `Book`, `Comment` экспонированы автоматически (`spring.data.rest.base-path: /api`) и отдают ответы в HAL с `_links`
- Spring Boot Actuator (spring-boot-starter-actuator) — healthchecks (`/actuator/health`), метрики (`/actuator/metrics`), logfile (`/actuator/logfile`); свой HealthCheck-индикатор `BookRepositoryHealthIndicator` (проверяет доступность данных книг)
- Spring Security — JWT-авторизация: stateless-конфигурация, `JwtService` (выпуск/проверка токенов), `JwtAuthenticationFilter`; разграничение доступа через аннотацию `@PreAuthorize`; `/api/users` — только `ROLE_ADMIN`
- JUnit 5, AssertJ, `@DataJpaTest`/`@SpringBootTest` — тесты репозиториев и сервисов на H2 с Hibernate-диалектом (`org.hibernate.dialect.H2Dialect`)
- `@Transactional` — только на методах сервисов

## Требования

- Java 17+
- Maven 3.6+

## Сборка и тесты

```bash
mvn clean test
```

## Структура приложения

- `ru.otus.spring.domain` — доменные классы `Author`, `Genre`, `Book`, `Comment`, `User`
- `ru.otus.spring.repository` — слой репозиториев Spring Data JPA: `AuthorRepository`, `GenreRepository`, `BookRepository`, `CommentRepository` (производные запросы и `@Query` с `join fetch` при `@Fetch(FetchMode.SUBSELECT)`), `UserRepository`
- `ru.otus.spring.service` — сервисный слой: `AuthorService`, `GenreService`, `BookService`, `CommentService`, `UserService` (также реализует `UserDetailsService`)
- `ru.otus.spring.dto` — DTO для REST API: `AuthorDto`, `GenreDto`, `BookDto`, `CommentDto`, `UserDto`
- `ru.otus.spring.repository` — слой репозиториев Spring Data JPA + Spring Data REST: `AuthorRepository`, `GenreRepository`, `BookRepository`, `CommentRepository` экспонированы через `@RepositoryRestResource` (HATEOAS/HAL, endpoints `/api/authors`, `/api/genres`, `/api/books`, `/api/comments`), `UserRepository` — только внутренний (`exported = false`)
- `ru.otus.spring.controller` — REST API: `AuthController` (`POST /api/auth/login`), `UserController` (`/api/users`, только `ROLE_ADMIN`), `ExternalServiceController` (`/api/external/*` — прокси-gateway к `/api/*` через `RestClient` с пробросом входящего `Authorization`-токена)
- `ru.otus.spring.health` — `BookRepositoryHealthIndicator` — собственный HealthCheck-индикатор (Spring Boot Actuator)
- `ru.otus.spring.security` — `SecurityConfig` (stateless chain, `BCryptPasswordEncoder`, `@EnableMethodSecurity`), `JwtService` (JWT: выпуск и проверка токенов), `JwtAuthenticationFilter`, `AuthResponseDto`, `LoginDto`; доступ к API — `@PreAuthorize` на контроллерах и репозиториях

## Запуск

```bash
mvn spring-boot:run
```

После старта Liquibase автоматически создаст таблицы `authors`, `genres`, `books`, заполнит их начальными данными и поднимет веб-приложение с REST API.

Консоль H2 доступна по адресу `http://localhost:8080/h2-console`:

- JDBC URL: `jdbc:h2:mem:librarydb`
- User: `sa`
- Password: (пусто)

## Docker

Multi-stage `Dockerfile` (build: `maven:3.9-eclipse-temurin-17`, runtime: `eclipse-temurin:17-jre`):

```bash
docker compose up -d --build   # собрать и запустить
docker compose ps             # статус (healthcheck через Tomcat/HTTP на 8080)
docker logs -f spring-library # логи в stdout
docker compose down          # остановить
```

Best Practices: non-root user, логгирование в stdout (профиль `docker` отключает file-logging), healthcheck, H2-файл в volume `library-data:/data`, `restart: unless-stopped`, H2-консоль отключена.

> БД in-memory: при каждом перезапуске структура и данные восстанавливаются из Liquibase.

## Начальные данные

Авторы:

| id | Фамилия | Имя |
|---|---|---|
| 1 | Barnes | Julian |
| 2 | Christie | Agatha |
| 3 | Gaiman | Neil |
| 4 | Rowling | Joanne |
| 5 | Orwell | George |

Жанры:

| id | Название |
|---|---|
| 1 | Novel |
| 2 | Detective |
| 3 | Fantasy |
| 4 | Adventure |
| 5 | Dystopia |

Книги (видна связь «у книги один автор и один жанр», автор может иметь несколько книг):

| id | Название | Автор | Жанр |
|---|---|---|---|
| 1 | The Sense of an Ending | Barnes | Novel |
| 2 | Murder on the Orient Express | Christie | Detective |
| 3 | The Ocean at the End of the Lane | Gaiman | Fantasy |
| 4 | Harry Potter and the Philosopher's Stone | Rowling | Fantasy |
| 5 | The Cuckoo's Calling | Rowling | Detective |
| 6 | Animal Farm | Orwell | Dystopia |
| 7 | 1984 | Orwell | Dystopia |

Комментарии:

| id | Текст | Книга |
|---|---|---|
| 1 | A moving novel about memory and aging | The Sense of an Ending (id 1) |
| 2 | The first book about the young wizard | Harry Potter and the Philosopher's Stone (id 4) |

## REST API (HATEOAS)

REST-слой построен на принципах HATEOAS через Spring Data REST (HAL, `spring.data.rest.base-path: /api`). Ответы содержат `_links`, связи объектов — гипермедиа-ссылками:

| Ресурс | Пример | Описание |
|---|---|---|
| `GET /api/authors` | `/api/authors` | список авторов (`_embedded.authors[]` + `_links.self`) |
| `GET /api/books`, `GET /api/comments`, `GET /api/genres` | `/api/books` | списки с `_links`, у книг связи `author`/`genre`, у комментариев `book` |
| `POST /api/authors` | `{"firstName","lastName"}` | создать; создание книги — `{"title","author":"/api/authors/5","genre":"/api/genres/5"}`, комментария — `{"text","book":"/api/books/1"}` |
| `PUT /api/authors/{id}`, `DELETE /api/authors/{id}` | полное обновление / удаление | `PUT /api/books/{id}` — `{"title","author":"…","genre":"…"}` |
| `/api/books/search` | поиск | производные запросы (`findAllByBookId` и т.п.) |

Пользователи (доступно только администратору): `/api/users`

Пользователи (доступно только администратору): `/api/users`

| Метод | URL | Описание |
|---|---|---|
| `GET` | `/api/users` | список всех пользователей (`UserDto`: id, username, role) |
| `GET` | `/api/users/{id}` | пользователь по id |
| `POST` | `/api/users` | создать пользователя (`{"username", "password"}`) |
| `PUT` | `/api/users/{id}` | изменить пользователя (`{"username", "password"}`) |
| `DELETE` | `/api/users/{id}` | удалить пользователя |

## Аутентификация на JWT

Учётные записи, создаваемые при старте (Liquibase `003-create-user.yaml`):

| Логин | Пароль | Роли |
|---|---|---|
| `admin` | `admin` | `ROLE_ADMIN` |
| `user` | `user` | `ROLE_USER` |

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

```bash
curl http://localhost:8080/api/books -H "Authorization: Bearer <JWT>"
```

Разграничение доступа задано аннотацией `@PreAuthorize`:

- `/api/users/**` — только `ROLE_ADMIN`, остальным — `403`.
- CRUD-эндпоинты `/api/authors`, `/api/genres`, `/api/books`, `/api/comments` — `ROLE_ADMIN` и `ROLE_USER`.
- Без токена или с недействительным токеном — `401`.

Токен подписан HMAC-SHA256 (`app.jwt.secret`), срок жизни — `app.jwt.expiration-ms` (по умолчанию 1 час).

## ExternalServiceController (прокси/gateway)

`ExternalServiceController` имитирует внешний сервис, который обращается к этому API: его эндпоинты внутри сами себя вызывают через `RestClient` (реальный HTTP-запрос на тот же инстанс), пробрасывая заголовок `Authorization` из входящего запроса. Авторизация работает по умолчанию (`@PreAuthorize("hasAnyRole('ADMIN','USER')")`).

| Эндпоинт | Внутренний вызов |
|---|---|
| `GET /api/external/books?page&size` | `GET /api/books?page&size` |
| `GET /api/external/books/{id}` | `GET /api/books/{id}` |
| `POST /api/external/books` | `POST /api/books` |
| `DELETE /api/external/books/{id}` | `DELETE /api/books/{id}` |
| `GET /api/external/authors` | `GET /api/authors` |
| `GET /api/external/genres` | `GET /api/genres` |
| `GET /api/external/users` | `GET /api/users` (admin) |

Spring Boot Actuator включён и отдаёт:

| Endpoint | Описание |
|---|---|
| `GET /actuator/health` | healthchecks (`db`, `diskSpace`, `bookRepository`) — открыт без токена |
| `GET /actuator/health` (подробно) | подробности статуса, включая собственный `BookRepositoryHealthIndicator` (см. `show-details: always`) |
| `GET /actuator/metrics` | метрики (JVM, JPA, HTTP и др.) |
| `GET /actuator/logfile` | содержимое лог-файла (`logging.file.name`) |

Все endpoint'ы Actuator, кроме `/actuator/health`, требуют аутентификации (JWT-токен).


```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT>" \
  -d '{"title":"Nineteen Eighty-Four","author":"/api/authors/5","genre":"/api/genres/5"}'
```

## Swagger UI

Swagger UI доступен по адресу `http://localhost:8080/swagger-ui.html` — можно просмотреть OpenAPI-документацию и выполнять запросы к API прямо из браузера:

- `GET /v3/api-docs` — спецификация OpenAPI (JSON)
- `GET /swagger-ui.html` (или `/swagger-ui/index.html`) — интерактивная консоль запросов
