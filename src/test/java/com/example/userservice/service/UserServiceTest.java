package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
    void create_shouldCreateUser_whenValidData() {
        User user = userService.create("Иван", "ivan@test.ru", 25);

        verify(userDao, times(1)).save(any(User.class));

        assertEquals("Иван", user.getName());
        assertEquals("ivan@test.ru", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void create_shouldThrowException_whenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.create("   ", "ivan@test.ru", 25)
        );

        verifyNoInteractions(userDao);
    }

    @Test
    void delete_shouldCallDaoWithId() {
        userService.delete(10L);
        verify(userDao, times(1)).delete(10L);
        verifyNoMoreInteractions(userDao);
    }


    @Test
    @DisplayName("getById: возвращает пользователя, если он найден в DAO")
    void getById_shouldReturnUser_whenUserExists() {

        long id = 1L;

        User user = new User();
        user.setId(id);
        user.setName("Иван");
        user.setEmail("ivan@mail.ru");
        user.setAge(20);

        when(userDao.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getById(id);

        assertTrue(result.isPresent());
        assertEquals("Иван", result.get().getName());

        verify(userDao).findById(id);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    @DisplayName("getById: возвращает empty, если пользователь не найден")
    void getById_shouldReturnEmpty_whenUserNotExists() {

        long id = 100L;

        when(userDao.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = userService.getById(id);

        assertTrue(result.isEmpty());

        verify(userDao).findById(id);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    @DisplayName("getAll: возвращает список пользователей из DAO")
    void getAll_shouldReturnUsersFromDao() {

        User u1 = new User();
        u1.setId(1L);
        u1.setName("Иван");
        u1.setEmail("ivan@mail.ru");
        u1.setAge(20);

        User u2 = new User();
        u2.setId(2L);
        u2.setName("Анна");
        u2.setEmail("anna@mail.ru");
        u2.setAge(22);

        List<User> usersFromDao = List.of(u1, u2);

        when(userDao.findAll()).thenReturn(usersFromDao);

        List<User> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals("Иван", result.get(0).getName());
        assertEquals("Анна", result.get(1).getName());

        verify(userDao).findAll();
        verifyNoMoreInteractions(userDao);
    }

    @Test
    @DisplayName("update: обновляет пользователя и вызывает dao.update()")
    void update_shouldUpdateUser_whenUserExists() {

        long id = 5L;

        User existing = new User();
        existing.setId(id);
        existing.setName("Старое имя");
        existing.setEmail("old@mail.ru");
        existing.setAge(10);

        when(userDao.findById(id)).thenReturn(Optional.of(existing));

        userService.update(id, "Новое имя", "new@mail.ru", 30);

        assertEquals("Новое имя", existing.getName());
        assertEquals("new@mail.ru", existing.getEmail());
        assertEquals(30, existing.getAge());

        verify(userDao).findById(id);
        verify(userDao).update(existing);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    @DisplayName("update: выбрасывает исключение, если пользователь не найден")
    void update_shouldThrowException_whenUserNotExists() {

        long id = 999L;

        when(userDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                userService.update(id, "Имя", "email@mail.ru", 25)
        );

        verify(userDao).findById(id);
        verifyNoMoreInteractions(userDao);
    }

}