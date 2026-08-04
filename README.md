# file-service

Core-сервис проекта **Cloudey Storage** — микросервисного облачного файлового хранилища. Отвечает за загрузку/скачивание файлов, хранение метаданных, шаринг между пользователями и публикацию доменных событий для остальных сервисов системы.

Часть портфолио-проекта [`cloudey-storage`](https://github.com/cloudey-storage) — набора независимых микросервисов, демонстрирующих архитектурные паттерны распределённых систем.

## Архитектурная роль

```
Client → API Gateway → file-service → MinIO (файлы)
                                    → PostgreSQL (метаданные)
                                    → Redis (кэш метаданных)
                                    → Kafka (события: file.uploaded, file.deleted, file.shared)
                                                ↓
                                    notification-service (consumer)
```

`file-service` не занимается аутентификацией — JWT валидируется на уровне API Gateway, который прокидывает `userId` в заголовке `X-User-Id`. Это осознанное решение, устраняющее дублирование security-логики между сервисами.

## Применённые паттерны и решения

- **Outbox Pattern** — атомарность между записью метаданных в PostgreSQL и публикацией события в Kafka: обе операции происходят в одной транзакции через промежуточную таблицу `outbox_events`, отдельный поллер асинхронно доставляет события в Kafka
- **Cache-Aside** — метаданные файлов кэшируются в Redis, инвалидация при изменении/удалении
- **Database per Service** — `owner_id` в таблице `files` не имеет foreign key на таблицу `users` (она физически в другой БД, принадлежащей `user-service`) — целостность между сервисами поддерживается на уровне приложения, а не БД
- **Presigned URL** — скачивание файлов идёт напрямую из MinIO по временной подписанной ссылке, без проксирования байтов через сам сервис
- **Partial Index** — индекс на `outbox_events(processed) WHERE processed = false`, оптимизация под частый паттерн запроса "выбрать необработанные события"

## Стек

Java 21, Spring Boot 4.1, Spring Data JPA (Hibernate), PostgreSQL 16, Redis 7, Apache Kafka (KRaft, без Zookeeper), MinIO, Flyway, Maven, Testcontainers, JUnit 5, Lombok, Docker Compose.

## Схема данных

**`files`** — метаданные загруженных файлов (владелец, ключ в MinIO, размер, soft-delete флаг)

**`file_shares`** — записи о доступе к файлу для других пользователей (READ/WRITE)

**`outbox_events`** — служебная таблица для Outbox Pattern (тип события, payload, флаг обработки)

Миграции версионируются через Flyway (`src/main/resources/db/migration`), схема БД не генерируется автоматически (`ddl-auto: validate`).

## API

| Метод | Путь | Описание |
|---|---|---|
| `POST` | `/api/files` | Загрузка файла (multipart) |
| `GET` | `/api/files` | Список файлов текущего пользователя |
| `GET` | `/api/files/{id}` | Метаданные файла |
| `GET` | `/api/files/{id}/download-url` | Presigned URL для скачивания |
| `DELETE` | `/api/files/{id}` | Soft-delete файла |
| `POST` | `/api/files/{id}/share` | Расшарить файл другому пользователю |
| `DELETE` | `/api/files/{id}/share/{userId}` | Отозвать доступ |

## Локальный запуск

Поднять инфраструктуру (PostgreSQL, Redis, Kafka, MinIO):

```bash
docker compose up -d
```

Проверить готовность всех сервисов:

```bash
docker compose ps
```

Собрать и запустить приложение:

```bash
./mvnw spring-boot:run
```

Сервис поднимется на `http://localhost:8081`.

MinIO Console (для визуальной проверки бакетов): `http://localhost:9001` (`minioadmin` / `minioadmin`)

## Тестирование

```bash
./mvnw clean install
```

> **Известная проблема:** тест `contextLoads` временно отключён (`@Disabled`) — Testcontainers не находит Docker environment из JVM-процесса при запуске через IDE, несмотря на корректные права доступа к Docker-сокету. Отслеживается в [issue #4](../../issues/4).

## Статус разработки

Проект находится в активной разработке. Backlog и статус задач ведутся через [GitHub Projects](../../projects).

Реализовано:
- Базовая конфигурация проекта (`pom.xml`, `application.yml`)
- Инфраструктура для локальной разработки (`docker-compose.yml`)
- Flyway-миграции схемы БД
- Testcontainers-конфигурация для интеграционных тестов (частично — см. известную проблему выше)

В работе:
- Entity/Repository слой
- Бизнес-логика загрузки/скачивания файлов
- Интеграция с MinIO, Redis, Kafka
- REST API

## Связанные репозитории

- [`api-gateway`](https://github.com/cloudey-storage/api-gateway) — единая точка входа, JWT-валидация
- [`user-service`](https://github.com/cloudey-storage/user-service) — аутентификация и профили пользователей
- [`notification-service`](https://github.com/cloudey-storage/notification-service) — обработка событий file-service
