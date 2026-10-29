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

## REST API

Эндпоинты принимают и возвращают DTO. Авторы: `http://localhost:8080/api/authors`

| Метод | URL | Описание |
|---|---|---|
| `GET` | `/api/authors` | список всех авторов (`AuthorDto`) |
| `GET` | `/api/authors/{id}` | автор по id |
| `POST` | `/api/authors` | создать автора (`{"firstName", "lastName"}`) |
| `PUT` | `/api/authors/{id}` | изменить автора |
| `DELETE` | `/api/authors/{id}` | удалить автора |

Жанры: `/api/genres` — аналогично (`GenreDto`, `{"name"}`).

Книги: `/api/books`

| Метод | URL | Описание |
|---|---|---|
| `GET` | `/api/books` | список всех книг (`BookDto`: id, title, authorId, genreId) |
| `GET` | `/api/books/{id}` | книга по id |
| `POST` | `/api/books` | создать книгу (`{"title", "authorId", "genreId"}`) |
| `PUT` | `/api/books/{id}` | изменить книгу |
| `DELETE` | `/api/books/{id}` | удалить книгу |

Комментарии: `/api/comments`

| Метод | URL | Описание |
|---|---|---|
| `GET` | `/api/comments` | список всех комментариев (`CommentDto`: id, text, bookId) |
| `GET` | `/api/comments/{id}` | комментарий по id |
| `POST` | `/api/comments` | создать комментарий (`{"text", "bookId"}`) |
| `PUT` | `/api/comments/{id}` | изменить текст комментария |
| `DELETE` | `/api/comments/{id}` | удалить комментарий |

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

## Swagger UI

Swagger UI доступен по адресу `http://localhost:8080/swagger-ui.html` — можно просмотреть OpenAPI-документацию и выполнять запросы к API прямо из браузера:

- `GET /v3/api-docs` — спецификация OpenAPI (JSON)
- `GET /swagger-ui.html` (или `/swagger-ui/index.html`) — интерактивная консоль запросов
