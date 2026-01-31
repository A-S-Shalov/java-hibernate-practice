# User Service (Hibernate + PostgreSQL)

Консольное приложение на Java для работы с пользователями (CRUD) через Hibernate без Spring.

## Технологии
- Java 17
- Hibernate ORM
- PostgreSQL (Docker)
- Maven
- Logback

## Запуск базы данных

```bash
docker compose up -d
```
PostgreSQL запускается в Docker-контейнере. Таблица `users` создаётся автоматически через `init.sql`.
## Запуск приложения

Открыть проект в IntelliJ IDEA и запустить класс Main.

## Функции

- Создание пользователя
- Поиск по ID
- Просмотр всех пользователей
- Обновление
- Удаление

## Архитектура

- DAO слой
- Service слой
- Консольный интерфейс
- Транзакции через Hibernate

## Назначение проекта

Учебный проект для демонстрации:

- настройки Hibernate без Spring
- работы с PostgreSQL
- реализации DAO-паттерна
- управления транзакциями 