package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Юнит-тесты для UserService.
 * Здесь мы тестируем ТОЛЬКО логику сервиса,
 * а UserDao подменяем мок-объектом.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * Мок (заглушка) для UserDao.
     * Реальная база данных здесь НЕ используется.
     */
    @Mock
    private UserDao userDao;

    /**
     * Тестируемый сервис.
     * Mockito сам подставит сюда userDao.
     */
    @InjectMocks
    private UserService userService;

    @Test
    void create_корректныеДанные_пользовательСоздается() {
        // вызываем метод сервиса
        User user = userService.create("Иван", "ivan@test.ru", 25);

        // проверяем, что метод save был вызван ровно один раз
        verify(userDao, times(1)).save(any(User.class));

        // проверяем, что поля пользователя заполнены корректно
        assertEquals("Иван", user.getName());
        assertEquals("ivan@test.ru", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void create_пустоеИмя_выбрасываетсяИсключение() {
        // проверяем, что при пустом имени выбрасывается IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
                userService.create("   ", "ivan@test.ru", 25)
        );

        // DAO при этом вызываться не должен
        verifyNoInteractions(userDao);
    }

    @Test
    void delete_передаетIdВDao() {
        // вызываем метод сервиса
        userService.delete(10L);

        // проверяем, что сервис просто делегирует вызов в DAO
        verify(userDao, times(1)).delete(10L);
    }
}
