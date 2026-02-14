package com.example.userservice.dao;

import com.example.userservice.dao.impl.UserDaoImpl;
import com.example.userservice.entity.User;
import com.example.userservice.util.HibernateUtil;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

/** Интеграционный тест с Testcontainers.
* - На Windows + Docker Desktop возможна ошибка определения Docker окружения (docker_cli / 400).
* - В CI / Linux тест выполняется корректно.
*/
@DisabledOnOs(
        value = OS.WINDOWS,
        disabledReason = "Testcontainers на Windows + Docker Desktop может не находить Docker окружение (docker_cli / 400). " +
                "Тест предназначен для Linux/CI."
)


/**
 * Интеграционные тесты DAO-слоя.
 *
 * ВАЖНО:
 * - Тут используется настоящая PostgreSQL в Docker через Testcontainers.
 * - Это НЕ юнит-тест, потому что участвуют Hibernate + база данных.
 * - Для изоляции: перед каждым тестом очищаем таблицу users.
 */
@Testcontainers
class UserDaoImplIntegrationTest {

    /**
     * Контейнер PostgreSQL.
     * withInitScript("db/init.sql") выполнит SQL из src/test/resources/db/init.sql
     * и создаст таблицу users.
     */
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("user_service")
            .withUsername("app")
            .withPassword("app")
            .withInitScript("db/init.sql");

    private final UserDao userDao = new UserDaoImpl();

    @BeforeAll
    static void beforeAll() {
        // Подменяем настройки Hibernate на настройки контейнера.
        // Делать это нужно ДО первого вызова HibernateUtil.getSessionFactory().
        System.setProperty("hibernate.connection.url", POSTGRES.getJdbcUrl());
        System.setProperty("hibernate.connection.username", POSTGRES.getUsername());
        System.setProperty("hibernate.connection.password", POSTGRES.getPassword());

        // Инициализируем SessionFactory один раз на весь класс тестов
        HibernateUtil.getSessionFactory();
    }

    @AfterAll
    static void afterAll() {
        // Закрываем SessionFactory и чистим системные свойства,
        // чтобы не было влияния на другие тесты.
        HibernateUtil.shutdown();
        System.clearProperty("hibernate.connection.url");
        System.clearProperty("hibernate.connection.username");
        System.clearProperty("hibernate.connection.password");
    }

    @BeforeEach
    void cleanDatabase() throws Exception {
        // Полная изоляция тестов:
        // перед каждым тестом очищаем таблицу и сбрасываем автоинкремент id.
        try (Connection c = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword()
        );
             Statement s = c.createStatement()) {
            s.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void save_и_findById_работаютКорректно() {
        // Создаём пользователя
        User user = new User("Иван", "ivan@example.com", 25);

        // Сохраняем в базу
        userDao.save(user);

        // Проверяем, что Hibernate проставил id
        assertNotNull(user.getId());
        assertTrue(user.getId() > 0);

        // Читаем из базы
        Optional<User> found = userDao.findById(user.getId());

        // Проверяем результат
        assertTrue(found.isPresent());
        assertEquals("Иван", found.get().getName());
        assertEquals("ivan@example.com", found.get().getEmail());
        assertEquals(25, found.get().getAge());

        // createdAt должен проставляться базой (по умолчанию now())
        assertNotNull(found.get().getCreatedAt());
    }
}
