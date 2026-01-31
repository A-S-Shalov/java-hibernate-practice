package com.example.userservice;

import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.impl.UserDaoImpl;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import com.example.userservice.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // запуск Hibernate
        HibernateUtil.getSessionFactory();

        UserDao userDao = new UserDaoImpl();
        UserService service = new UserService(userDao);

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMenu();
                System.out.print("Выберите пункт: ");
                String choice = sc.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> createUser(sc, service);
                        case "2" -> findById(sc, service);
                        case "3" -> listAll(service);
                        case "4" -> updateUser(sc, service);
                        case "5" -> deleteUser(sc, service);
                        case "0" -> {
                            System.out.println("Выход из программы.");
                            return;
                        }
                        default -> System.out.println("Неизвестная команда. Попробуйте снова.");
                    }
                } catch (Exception e) {
                    System.out.println("ОШИБКА: " + e.getMessage());
                }

                System.out.println();
            }
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void printMenu() {
        System.out.println("==== СЕРВИС ПОЛЬЗОВАТЕЛЕЙ ====");
        System.out.println("1) Создать пользователя");
        System.out.println("2) Найти пользователя по id");
        System.out.println("3) Показать всех пользователей");
        System.out.println("4) Обновить пользователя");
        System.out.println("5) Удалить пользователя");
        System.out.println("0) Выход");
    }

    private static void createUser(Scanner sc, UserService service) {
        System.out.print("Имя: ");
        String name = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        int age = readInt(sc, "Возраст: ");

        User created = service.create(name, email, age);
        System.out.println("Пользователь создан: " + created);
    }

    private static void findById(Scanner sc, UserService service) {
        long id = readLong(sc, "Введите id: ");
        Optional<User> user = service.getById(id);
        System.out.println(user.orElse(null));
    }

    private static void listAll(UserService service) {
        List<User> users = service.getAll();
        if (users.isEmpty()) {
            System.out.println("Список пуст.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void updateUser(Scanner sc, UserService service) {
        long id = readLong(sc, "Введите id пользователя для обновления: ");

        System.out.print("Новое имя: ");
        String name = sc.nextLine().trim();

        System.out.print("Новый email: ");
        String email = sc.nextLine().trim();

        int age = readInt(sc, "Новый возраст: ");

        service.update(id, name, email, age);
        System.out.println("Пользователь обновлён.");
    }

    private static void deleteUser(Scanner sc, UserService service) {
        long id = readLong(sc, "Введите id пользователя для удаления: ");
        service.delete(id);
        System.out.println("Пользователь удалён (если существовал).");
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Введите число.");
            }
        }
    }

    private static long readLong(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }
    }
}
