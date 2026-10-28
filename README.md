# Spring Library

Приложение-библиотека: хранение книг, авторов и жанров в реляционной БД. Управление выполняется через интерфейс командной строки (Spring Shell): CRUD для книг, авторов и жанров.

## Запуск

```bash
mvn spring-boot:run
```

После старта Liquibase автоматически создаст таблицы `authors`, `genres`, `books`, заполнит их начальными данными и поднимет консоль Spring Shell:

```
shell:>
```

Выход из шелла — команда `exit`.

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

## Команды Spring Shell

Авторы:

| Команда | Описание |
|---|---|
| `author add --first-name <имя> --last-name <фамилия>` | добавить автора |
| `author list` | список всех авторов |
| `author get --id <id>` | показать автора по id |
| `author update --id <id> --first-name <имя> --last-name <фамилия>` | изменить автора |
| `author delete --id <id>` | удалить автора (если на него не ссылаются книги) |

Жанры:

| Команда | Описание |
|---|---|
| `genre add --name <название>` | добавить жанр |
| `genre list` | список всех жанров |
| `genre get --id <id>` | показать жанр по id |
| `genre update --id <id> --name <название>` | изменить жанр |
| `genre delete --id <id>` | удалить жанр (если на него не ссылаются книги) |

Книги:

| Команда | Описание |
|---|---|
| `book add --title <название> --author-id <id> --genre-id <id>` | добавить книгу (автор и жанр должны существовать) |
| `book list` | список всех книг с авторами и жанрами |
| `book get --id <id>` | показать книгу по id |
| `book update --id <id> --title <название> --author-id <id> --genre-id <id>` | изменить книгу |
| `book delete --id <id>` | удалить книгу (удаляет и комментарии книги) |

Комментарии к книгам:

| Команда | Описание |
|---|---|
| `comment add --book-id <id> --text <текст>` | добавить комментарий к книге |
| `comment list` | список всех комментариев |
| `comment get --id <id>` | показать комментарий по id |
| `comment update --id <id> --text <текст>` | изменить текст комментария |
| `comment delete --id <id>` | удалить комментарий |

## Пример сессии

```
shell:> book list
Book{id=1, The Sense of an Ending, author=[id=1, Barnes Julian], genre=[id=1, Novel]}
Book{id=4, Harry Potter and the Philosopher's Stone, author=[id=4, Rowling Joanne], genre=[id=3, Fantasy]}
...

shell:> book add --title "Nineteen Eighty-Four" --author-id 5 --genre-id 5
Book added: Book{...}

shell:> genre delete --id 4
Genre with id=4 removed
```
