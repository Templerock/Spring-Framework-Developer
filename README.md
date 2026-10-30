# spring-library-app

Учебный проект: реализация EIP (Enterprise Integration Patterns) на Spring Integration на примере превращения гусеницы в бабочку.

## Стек

- Spring Boot 3.3.11, Java 17
- Spring Integration (DSL), Spring Web

## EIP-компоненты

`ru.otus.spring.integration.IntegrationConfig`

| Компонент | Что делает |
|---|---|
| **Messaging Gateway** (`MetamorphosisGateway`) | точка входа: `String transform(Caterpillar)` → `metamorphosisInput` |
| **Filter** | пропускает гусениц с `leavesEaten >= 10`, остальных в `metamorphosisDiscarded` |
| **Transformer** | `Caterpillar` → `Cocoon` (`pupate`) |
| **Log** | логирует окукливание |
| **Router + subflows** | по `ButterflyActivity` (DAY/NIGHT) → `emergeDay`/`emergeNight` → `metamorphosisOutput` |
| **Transformer** | `Butterfly` → `String` (ответ, уходит в `replyChannel` ворот) |

## Запуск

```
mvn spring-boot:run
```

Пример запроса:

```
POST /api/metamorphosis
{"name":"Веня","species":"Sphinx","leavesEaten":42}
```

Ответ: `Кокон раскрылся: бабочка 'Веня' (Sphinx), окраска тускло-серая, активна ночью`.

## Тесты

```
mvn test
```
