# Spring Library

Приложение-библиотека: хранение книг, авторов, жанров и комментариев в реляционной БД. Управление выполняется через REST API (Spring MVC): CRUD для книг, авторов, жанров и комментариев.

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

Пример:

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Nineteen Eighty-Four","authorId":5,"genreId":5}'
```

## Swagger UI

Swagger UI доступен по адресу `http://localhost:8080/swagger-ui.html` — можно просмотреть OpenAPI-документацию и выполнять запросы к API прямо из браузера:

- `GET /v3/api-docs` — спецификация OpenAPI (JSON)
- `GET /swagger-ui.html` (или `/swagger-ui/index.html`) — интерактивная консоль запросов
