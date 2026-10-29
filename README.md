# Spring Library

Приложение-библиотека: хранение книг, авторов, жанров и комментариев в реляционной БД. Управление выполняется через REST API (Spring MVC): CRUD для книг, авторов, жанров, комментариев и пользователей. Авторизация — на JWT (Spring Security): клиент получает токен на `/api/auth/login` и передаёт его в заголовке `Authorization: Bearer <token>`. Ролевые ограничения заданы аннотацией `@PreAuthorize`.

## Запуск

```bash
mvn spring-boot:run
```

После старта Liquibase автоматически создаст таблицы `authors`, `genres`, `books`, заполнит их начальными данными и поднимет веб-приложение с REST API.

Консоль H2 доступна по адресу `http://localhost:8080/h2-console`:

- JDBC URL: `jdbc:h2:mem:librarydb`
- User: `sa`
- Password: (пусто)

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

## Actuator

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
