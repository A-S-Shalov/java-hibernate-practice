package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User create(String name, String email, int age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }

        User user = new User(name, email, age);
        userDao.save(user);
        return user;
    }

    public Optional<User> getById(long id) {
        return userDao.findById(id);
    }

    public List<User> getAll() {
        return userDao.findAll();
    }

    public void update(long id, String name, String email, int age) {
        User existing = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        existing.setName(name);
        existing.setEmail(email);
        existing.setAge(age);

        userDao.update(existing);
    }

    public void delete(long id) {
        userDao.delete(id);
    }
}
